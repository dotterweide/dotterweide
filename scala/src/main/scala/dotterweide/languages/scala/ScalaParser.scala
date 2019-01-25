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

import scala.annotation.tailrec
import scala.concurrent.Future
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
      settings.usejavacp.value              = true
      settings.YpresentationAnyThread.value = true // needed to print typed tree
      settings.source.value                 = ScalaVersion("2.12.8")

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
          log.info("done compile")

          type Parents = List[Global#Tree]

          @tailrec
          def offsetOf(parents: Parents): Int = parents match {
            case head :: tail =>
              head.pos match {
                case dp: DefinedPosition  => dp.start
                case _                    => offsetOf(tail)
              }
            case _ => 0
          }

          def complete(p: Global#Tree, parents: Parents, n: NodeImpl): n.type = {
            p.pos match {
              case dp: DefinedPosition =>
//                val off0      = offsetOf(parents)
                val start     = dp.start
                val off       = 0 // math.min(start, off0)
                val stop      = dp.end
                val spanText  = text.substring(start, stop)
//                val bad = if (start < off) "!" else " "
//                println(s"$bad ${n.kind} - start = $start, off = $off")
                n.span        = Span(spanText, start - off, stop - off)
//                n.span        = Span(spanText, start, stop)

              case _ =>
            }
            n
          }

          def parseTypeDef(p: Global#TypeDef, parents: Parents): TypeDefNode = {
            val parents1 = p :: parents
            // c.TypeDef(_ /* mods: Modifiers */, _ /* name: TypeName */, tParams /* : List[TypeDef] */, rhs /* : Tree */)
            val tParamNodes = p.tparams.map(parseTypeDef(_, parents1))
            val rhsNode     = parseChild(p, p.rhs, parents)
            complete(p, parents, new TypeDefNode(tParamNodes, rhsNode))
          }

          def parseValDef(p: Global#ValDef, parents: Parents): ValDefNode = {
            // c.ValDef(_ /* mods: Modifiers */, _ /* name: TermName */, tpt /* :Tree */, rhs /* :Tree */) =>
            val tptNode = parseChild(p, p.tpt, parents)
            val rhsNode = parseChild(p, p.rhs, parents)
            complete(p, parents, new ValDefNode(tptNode, rhsNode))
          }

          def parseIdent(p: Global#Ident, parents: Parents): IdentNode = {
            // c.Ident(_ /* name: Name */)
            complete(p, parents, new IdentNode)
          }

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
            complete(p, parents, new TemplateNode(parentNodes, selfNode, bodyNodes))
          }

          def parseChild(p: Global#Tree, c: Global#Tree, parents: List[Global#Tree]): NodeImpl = {
            val parents1 = p :: parents
            complete(c, parents1, parse(c, parents1))
          }

          def parse(p: Global#Tree, parents: List[Global#Tree]): NodeImpl = {
            @inline def parents1: Parents = p :: parents

            @inline
            def parseChild1(c: Global#Tree): NodeImpl =
              parseChild(p, c, parents)

//            log.info(s"-- ${"  " * indent}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
            val resNode: NodeImpl = p match {
//              case c.Alternative      (_)     =>
//              case c.Annotated        (_, _)  =>
//              case c.AppliedTypeTree  (_, _)  =>
//              case c.ApplyDynamic     (_, _)  =>
//              case c.ArrayValue       (_, _)  =>
//              case c.AssignOrNamedArg (_, _)  =>

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

              case c.Block(init /* :List[Tree] */, last /* :Tree */) =>
                val initNodes = init.map { child =>
                  parseChild1(child)
                }
                val lastNode = parseChild1(last)
                new BlockNode(initNodes, lastNode)

              case c.DefDef(_ /* mods: Modifiers */, _ /* name: TermName */, tParams /* :List[TypeDef] */,
                            vParamsS /* :List[List[ValDef]] */, tpt /* :Tree */, rhs /* :Tree */) =>
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
                new DefDefNode(tParamNodes, vParamNodesS, tptNode, rhsNode)

              case c.EmptyTree =>
                new EmptyNode

              case in: c.Ident => parseIdent(in, parents)

              case c.If(cond /* :Tree */, thenP /* :Tree */, elseP /* :Tree */) =>
                val condNode  = parseChild1(cond )
                val thenNode  = parseChild1(thenP)
                val elseNode  = parseChild1(elseP)
                new IfNode(condNode, thenNode, elseNode)

              case c.LabelDef(_ /* name: TermName */, params /* :List[Ident] */, rhs /* :Tree */) =>
                val paramNodes = params.map { child =>
                  parseIdent(child, parents1)
                }
                val rhsNode = parseChild1(rhs)
                new LabelDefNode(paramNodes, rhsNode)

              case c.Literal(_ /* value: Constant */) =>
                new LiteralNode

              case c.ModuleDef(_ /* mods: Modifiers */, _ /* name: TermName */, child /* :Template */) =>
                val childNode = parseTemplate(child, parents1)
                new ModuleDefNode(childNode)

              case c.PackageDef(pid /* :RefTree */, stats /* :List[Tree] */) =>
                val pidNode     = parseChild1(pid)
                val statNodes   = stats.map { child =>
                  parseChild1(child)
                }
                new PackageDefNode(pidNode, statNodes)

              case c.Select(child /* :Tree */, _ /* name: Name */) =>
                val childNode = parseChild1(child)
                new SelectNode(childNode)

              case c.Super(qualifier /* :Tree */, _ /* mix: TypeName */) =>
                val qNode = parseChild1(qualifier)
                new SuperNode(qNode)

              case tn: c.Template => parseTemplate(tn, parents)

              case c.This(_ /* qualifier: TypeName */) =>
                new ThisNode

              case td: c.TypeDef => parseTypeDef(td, parents)

              case c.TypeTree() =>
                new TypeTreeNode

              case vd: c.ValDef => parseValDef(vd, parents)

              case _ =>
                log.info(s"-- SKIP ${"  " * parents.size}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
                new NodeImpl("<unknown>")
            }
            resNode
          }
          val programNode = parse(tree, Nil)
          complete(tree, Nil, programNode)

//          val n = new PackageDefNode()
//          n.children = reporter.infos.iterator.filter(info => info.pos.isDefined && info.severity.id >= 2).map { info =>
//            val child     = new BlockNode()
//            import info.pos._
//            log.info(s"info [$start, $end]")
//            child.span    = Span("", start, end)
//            child.problem = Some(info.msg)
//            child
//          } .toVector // .sortBy(_.span.begin)

          programNode

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
