/*
 *  ScalaParser.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.Logging
import akka.pattern.ask
import akka.util.Timeout
import dotterweide.Span
import dotterweide.editor.Async
import dotterweide.languages.scala.node._
import dotterweide.lexer.Token
import dotterweide.node.{Node, NodeImpl}
import dotterweide.parser.Parser

import scala.collection.mutable
import scala.concurrent.Future
import scala.reflect.api.Position
import scala.reflect.internal.util.DefinedPosition
import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion
import scala.util.{Failure, Success}

/** The parser for the Scala language runs a compiler instance in an Akka actor
  * (thus single threaded) which may be queried for nodes.
  */
class ScalaParser extends Parser {
  private val system                  = ActorSystem("ScalaParser")
  private val compilerActor: ActorRef = system.actorOf(Props(new CompilerActor), "compiler")

  private case class Compile(text: String)

  private class CompilerActor extends Actor {
    private val log = Logging(context.system, this)

    private val reporter = new StoreReporter

    private lazy val c: Global = {
      val outputDir = new VirtualDirectory("<virtual-dir>", None)
      val settings  = new Settings(err => Console.err.println(err))

      settings.outputDirs.setSingleOutput(outputDir)
      settings.usejavacp              .value  = true
      settings.YpresentationAnyThread .value  = true // needed to print typed tree
      settings.Yrangepos              .value  = true
//      settings.Yvalidatepos           .value  = "analyze" :: Nil
      settings.source                 .value  = ScalaVersion("2.12.8")

      new Global(settings, reporter)
    }

    private def compile(text: String): c.Tree = {
      import c._
      val srcFile   = newSourceFile(text)
      val respTypes = new Response[c.Tree]
      c.askReset()
      c.askLoadedTyped(srcFile, keepLoaded = false /* true */, respTypes)
      val treeTyped: c.Tree = respTypes.get.left.get
      treeTyped
    }

//    private def parseTo(p: Global#Tree, indent: Int)(implicit b: TreeBuilder): Unit = {
//
//    }

    def receive: Receive = {
      case Compile(text) =>
        val reply = try {
          log.info("begin compile")
          val tree: c.Tree = compile(text)
          // log.info("done compile")

          type Parents = List[Global#Tree]

//          @tailrec
//          def offsetOf(parents: Parents): Int = parents match {
//            case head :: tail =>
//              head.pos match {
//                case dp: DefinedPosition  => dp.start
//                case _                    => offsetOf(tail)
//              }
//            case _ => 0
//          }

          def setPosition(n: NodeImpl, pos: Position, inclusive: Boolean = false): n.type = {
            val start     = pos.start
            val stop      = if (inclusive) pos.end + 1 else pos.end
            val spanText  = text.substring(start, stop)
            n.span        = Span(spanText, start, stop)
            n
          }

          val nameCache = mutable.Set.empty[HasNameNode]
          val refCache  = mutable.Set.empty[IsRef]

          def complete(p: Global#Tree, parents: Parents, n: NodeImpl): n.type = {
            p.pos match {
              case dp: DefinedPosition => setPosition(n, dp)
              case _ =>
            }
            n match {
              case hn: IsDef  => nameCache  += hn
              case hr: IsRef  => refCache   += hr
              case _ =>
            }
            n
          }

          def mkMods(mods: Global#Modifiers): List[ModifierNode] =
            mods.positions.iterator.filter(_._2.isDefined).toList.sortBy(_._2.start).map {
              case (code, pos) =>
                val nm = new ModifierNode(code)
                setPosition(nm, pos, inclusive = true /* WTF */)
            }

          val symCache = mutable.Map.empty[Global#Symbol, NameNode]

          def mkSymName(sym: Global#Symbol): NameNode = symCache.getOrElseUpdate(sym,
            mkName(sym.pos, sym.name)
          )

          def mkName(pos: Position, name: Global#Name): NameNode = {
            val s = name.decoded.trim // WTF `trim`, there are dangling trailing spaces
            // println(s"'$s'")
            val n = new NameNode(s)
            pos match {
              case pd: DefinedPosition /* if pos.isOpaqueRange */ =>
                n.span =
//                  if (leftAligned) {
                  Span(s, pd.point, pd.point + s.length)
//                } else {
//                  Span(s, pos.end - s.length, pos.end)
//                }
              case _ =>
            }
            n
          }

          def parseCaseDef(p: Global#CaseDef, parents: Parents): CaseDefNode = {
            // c.CaseDef(pat: Tree, guard: Tree, body: Tree)
            val patNode   = parseChild(p, p.pat   , parents)
            val guardNode = parseChild(p, p.guard , parents)
            val bodyNode  = parseChild(p, p.body  , parents)
            val n         = new CaseDefNode(patNode, guardNode, bodyNode)
            complete(p, parents, n)
          }

          def parseIdent(p: Global#Ident, parents: Parents): IdentNode = {
            // c.Ident(_ /* name: Name */)
            val nameNode  = mkSymName(p.symbol)
            val n         = new IdentNode(nameNode)
            complete(p, parents, n)
          }

//          def parseImportSelector(p: Global#ImportSelector, parents: Parents): IdentNode = {
//            // c.ImportSelector(_ /* name: Name */, _ /* namePos: Int */, _ /* rename: Name */, _ /* renamePos: Int */)
//            val n = new ImportSelectorNode
//            complete(p, parents, n)
//          }

          def parseTemplate(p: Global#Template, parents: Parents): TemplateNode = {
            val parents1 = p :: parents
            // c.Template(parents /* :List[Tree] */, valDef /* :ValDef */, stats /* :List[Tree] */) =>
            val parentNodes = p.parents.map { child =>
              parseChild(p, child, parents)
            }
            val selfNode  = parseValDef(p.self , parents1)
            val bodyNodes = p.body.map { child =>
              parse(child, parents1)
            }
            val n         = new TemplateNode(parentNodes, selfNode, bodyNodes)
            complete(p, parents, n)
          }

          def parseTypeDef(p: Global#TypeDef, parents: Parents): TypeDefNode = {
            val parents1 = p :: parents
            // c.TypeDef(_ /* mods: Modifiers */, _ /* name: TypeName */, tParams /* : List[TypeDef] */, rhs /* : Tree */)
            val tParamNodes = p.tparams.map(parseTypeDef(_, parents1))
            val rhsNode     = parseChild(p, p.rhs, parents)
            val n           = new TypeDefNode(tParamNodes, rhsNode)
            complete(p, parents, n)
          }

          def parseValDef(p: Global#ValDef, parents: Parents): ValDefNode = {
            // c.ValDef(_ /* mods: Modifiers */, _ /* name: TermName */, tpt /* :Tree */, rhs /* :Tree */) =>
            // println(s"VAL DEF ${p.name} - mods = ${p.mods}; name.start ${p.name.start}; name.len ${p.name.length()}")

            // XXX TODO --- need to add defaults
            val modNodes  = mkMods(p.mods)
            val nameNode  = mkSymName(p.symbol)
            val tptNode   = parseChild(p, p.tpt, parents)
            val rhsNode   = parseChild(p, p.rhs, parents)
            val n         = new ValDefNode(modNodes, nameNode, tptNode, rhsNode)
            complete(p, parents, n)
          }

          def parseChild(p: Global#Tree, c: Global#Tree, parents: List[Global#Tree]): NodeImpl = {
            val parents1  = p :: parents
            val n         = parse(c, parents1)
            complete(c, parents1, n)
          }

          def parse(p: Global#Tree, parents: List[Global#Tree]): NodeImpl = {
            @inline def parents1: Parents = p :: parents

            @inline
            def parseChild1(c: Global#Tree): NodeImpl =
              parseChild(p, c, parents)

//            log.info(s"-- ${"  " * indent}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
            val res: NodeImpl = p match {
//              case c.Alternative      (_)     =>
//              case c.Annotated        (_, _)  =>
//              case c.ApplyDynamic     (_, _)  =>
//              case c.ArrayValue       (_, _)  =>

              case c.AppliedTypeTree(tpt /* :Tree */, args /* :List[Tree] */) =>
                val tgtNode   = parseChild1(tpt)
                val argNodes  = args.map { child =>
                  parseChild1(child)
                }
                new AppliedTypeTreeNode(tgtNode, argNodes)

              case c.Apply(receiver /* :Tree */, args /* :List[Tree] */) =>
                val rcvNode   = parseChild1(receiver)
                val argNodes  = args.map { child =>
                  parseChild1(child)
                }
                new ApplyNode(rcvNode, argNodes)

              case c.Assign(lhs /* :Tree */, rhs /* :Tree */) =>
                val lhsNode = parseChild1(lhs)
                val rhsNode = parseChild1(rhs)
                new AssignNode(lhsNode, rhsNode)

              case c.AssignOrNamedArg(lhs /* :Tree */, rhs /* :Tree */) =>
                val lhsNode = parseChild1(lhs)
                val rhsNode = parseChild1(rhs)
                new AssignOrNamedArgNode(lhsNode, rhsNode)

              case c.Bind(_ /* name: Name */, body /* :Tree */) =>
                val nameNode  = mkSymName(p.symbol)
                val bodyNode  = parseChild1(body)
                new BindNode(nameNode, bodyNode)

              case c.Block(init /* :List[Tree] */, last /* :Tree */) =>
                val initNodes = init.map { child =>
                  parseChild1(child)
                }
                val lastNode = parseChild1(last)
                new BlockNode(initNodes, lastNode)

              case c.ClassDef(mods /* Modifiers */, _ /* name: TypeName */, tParams /* List[TypeDef] */,
                              impl /* :Template */) =>
                val modNodes    = mkMods(mods)
                val nameNode    = mkSymName(p.symbol)
                val tParamNodes = tParams.map { child =>
                  parseTypeDef(child, parents1)
                }
                val childNode = parseTemplate(impl, parents1)
                new ClassDefNode(modNodes, nameNode, tParamNodes, childNode)

              case c.DefDef(mods /* :Modifiers */, _ /* name: TermName */, tParams /* :List[TypeDef] */,
                            vParamsS /* :List[List[ValDef]] */, tpt /* :Tree */, rhs /* :Tree */) =>
                // println(s"def-def symbol = ${p.symbol}")
                // p.symbol.pos
                val modNodes    = mkMods(mods)
                val nameNode    = mkSymName(p.symbol)
                val tParamNodes = tParams.map { child =>
                  parseTypeDef(child, parents1)
                }
                val vParamNodesS = vParamsS.map { vParams =>
                  vParams.map { child =>
                    parseValDef(child, parents1)
                  }
                }
                val tptNode = parseChild1(tpt)
                val rhsNode = parseChild1(rhs)
                new DefDefNode(modNodes, nameNode, tParamNodes, vParamNodesS, tptNode, rhsNode)

              case c.EmptyTree =>
                new EmptyNode

              case c.Function(vParams /* :List[ValDef] */, body /* :Tree */) =>
                val vParamNodes = vParams.map { child =>
                    parseValDef(child, parents1)
                  }
                val bodyNode = parseChild1(body)
                new FunctionNode(vParamNodes, bodyNode)

              case in: c.Ident => parseIdent(in, parents)

              case c.If(cond /* :Tree */, thenP /* :Tree */, elseP /* :Tree */) =>
                val condNode  = parseChild1(cond )
                val thenNode  = parseChild1(thenP)
                val elseNode  = parseChild1(elseP)
                new IfNode(condNode, thenNode, elseNode)

              case c.Import(expr /* :Tree */, _ /* sel: List[ImportSelector] */) =>
                val exprNode  = parseChild1(expr)
//                val selNodes = sel.map { child =>
//                  parseImportSelector(child, parents1)
//                }
                new ImportNode(exprNode)

              case c.LabelDef(_ /* name: TermName */, params /* :List[Ident] */, rhs /* :Tree */) =>
                val paramNodes = params.map { child =>
                  parseIdent(child, parents1)
                }
                val rhsNode = parseChild1(rhs)
                new LabelDefNode(paramNodes, rhsNode)

              case c.Literal(const /* :Constant */) =>
                new LiteralNode(const.value)

              case c.Match(sel /* :Tree */, cases /* :List[CaseDef] */) =>
                val selNode = parseChild1(sel)
                val caseNodes = cases.map { child =>
                  parseCaseDef(child, parents1)
                }
                new MatchNode(selNode, caseNodes)

              case c.ModuleDef(mods /* Modifiers */, _ /* name: TermName */, child /* :Template */) =>
                val modNodes  = mkMods(mods)
//                if (p.pos.isRange) {
//                  val namePos = p.pos.asInstanceOf[RangePosition]
//                  namePos.point
//                }
                val nameNode  = mkSymName(p.symbol)
                val childNode = parseTemplate(child, parents1)
                new ModuleDefNode(modNodes, nameNode, childNode)

              case c.New(tpt /* :Tree */ ) =>
                val tptNode = parseChild1(tpt)
                new NewNode(tptNode)

              case c.PackageDef(pid /* :RefTree */, stats /* :List[Tree] */) =>
                val pidNode     = parseChild1(pid)
                val statNodes   = stats.map { child =>
                  parseChild1(child)
                }
                new PackageDefNode(pidNode, statNodes)

              case c.Return(expr /* :Tree */) =>
                val exprNode = parseChild1(expr)
                new ReturnNode(exprNode)

              case /* in @ */ c.Select(qualifier /* :Tree */, name /* :Name */) =>
                // Note: interesting thing; if we use `p.symbol`, we find the definition site!
                // val nameNode      = mkName(p.symbol)
                val nameNode      = mkName(p.pos, name)
                val qualifierNode = parseChild1(qualifier)
                new SelectNode(qualifierNode, nameNode)

              case c.Super(qualifier /* :Tree */, _ /* mix: TypeName */) =>
                val qNode = parseChild1(qualifier)
                new SuperNode(qNode)

              case tn: c.Template => parseTemplate(tn, parents)

              case c.This(_ /* qualifier: TypeName */) =>
                new ThisNode

              case c.Throw(expr /* :Tree */) =>
                val exprNode = parseChild1(expr)
                new ThrowNode(exprNode)

              case c.Try(block /* :Tree */, cases /* :List[CaseDef] */, finalizer /* :Tree */) =>
                val blockNode = parseChild1(block)
                val caseNodes = cases.map { child =>
                  parseCaseDef(child, parents1)
                }
                val finalizerNode = parseChild1(finalizer)
                new TryNode(blockNode, caseNodes, finalizerNode)

              case c.TypeApply(fun /* :Tree */, args /* :List[Tree] */) =>
                val funNode     = parseChild1(fun)
                val argNodes    = args.map { child =>
                  parseChild1(child)
                }
                new TypeApplyNode(funNode, argNodes)

              case c.Typed(expr /* :Tree */, tpt /* :Tree */) =>
                val exprNode  = parseChild1(expr)
                val tptNode   = parseChild1(tpt)
                new TypedNode(exprNode, tptNode)

              case td: c.TypeDef => parseTypeDef(td, parents)

              case c.TypeTree() =>
                new TypeTreeNode

              case c.UnApply(fun /* :Tree */, args /* :List[Tree] */) =>
                val funNode     = parseChild1(fun)
                val argNodes    = args.map { child =>
                  parseChild1(child)
                }
                new UnApplyNode(funNode, argNodes)

              case vd: c.ValDef => parseValDef(vd, parents)

              case _ =>
                log.info(s"-- SKIP ${"  " * parents.size}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
                new NodeImpl("<unknown>")
            }
            res
          }

          val programNode = parse(tree, Nil)
          complete(tree, Nil, programNode)
          // log.info("done tree")

          // now assign the reference targets
          refCache.foreach { rn =>
            rn.target
          }

          var moreErrors = List.empty[NodeImpl]

          reporter.infos.iterator.filter(info => info.pos.isDefined && info.severity.id >= 2).foreach { info =>
            val sp = Span("", info.pos.start, info.pos.end)

            val childOpt = programNode.elements.find(_.span.matches(sp))
            childOpt match {
              case Some(child) =>
                child.problem = Some(info.msg)
              case None =>
                // log.info(s"no span for error: ${info.msg}")
                moreErrors ::= NodeImpl.createError(None, sp, info.msg)
            }
          }

          log.info("done errors")

          if (moreErrors.isEmpty) {
            programNode
          } else {
            val top       = new NodeImpl("top")
            top.children  = programNode :: moreErrors.reverse
            top
          }

        } catch {
          case e: Exception => akka.actor.Status.Failure(e)
        }
        sender() ! reply

      case m =>
        log.error(s"Unknown message $m")
    }
  }

  def parseAsync(text: String, tokens: Iterator[Token])(implicit async: Async): Future[Node] = {
     import async.executionContext

    val fut = compilerActor.ask(Compile(text))(Timeout(5, TimeUnit.SECONDS)).mapTo[Node]
    fut.onComplete {
      case Success(_) =>
      case Failure(ex) =>
        println("Compilation failed:")
        ex.printStackTrace()
    }
    fut
  }
}
