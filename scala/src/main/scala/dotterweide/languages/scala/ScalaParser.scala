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

          def complete(p: Global#Tree, n: NodeImpl): n.type = {
            p.pos match {
              case dp: DefinedPosition =>
                val start     = dp.start
                val stop      = dp.end
                val spanText  = text.substring(start, stop)
                n.span        = Span(spanText, start, stop)

              case _ =>
            }
            n
          }

          def parseTypeDef(p: Global#TypeDef, indent: Int): TypeDefNode = {
            val indent1 = indent + 1
            // c.TypeDef(_ /* mods: Modifiers */, _ /* name: TypeName */, tParams /* : List[TypeDef] */, rhs /* : Tree */)
            val tParamNodes = p.tparams.map(parseTypeDef(_, indent1))
            val rhsNode     = parse(p.rhs, indent = indent1)
            new TypeDefNode(tParamNodes, rhsNode)
          }

          def parseValDef(p: Global#ValDef, indent: Int): ValDefNode = {
            val indent1 = indent + 1
            // c.ValDef(_ /* mods: Modifiers */, _ /* name: TermName */, tpt /* :Tree */, rhs /* :Tree */) =>
            val tptNode = parse(p.tpt, indent1)
            val rhsNode = parse(p.rhs, indent1)
            new ValDefNode(tptNode, rhsNode)
          }

          def parseIdent(p: Global#Ident, indent: Int): IdentNode = {
            // c.Ident(_ /* name: Name */)
            new IdentNode
          }

          def parseTemplate(p: Global#Template, indent: Int): TemplateNode = {
            val indent1 = indent + 1
            // c.Template(parents /* :List[Tree] */, valDef /* :ValDef */, stats /* :List[Tree] */) =>
            val parentNodes = p.parents.map { child =>
              parse(child, indent1)
            }
            val selfNode  = parseValDef(p.self , indent1)
            val bodyNodes = p.body.map { child =>
              parse(child, indent1)
            }
            new TemplateNode(parentNodes, selfNode, bodyNodes)
          }

          def parse(p: Global#Tree, indent: Int): NodeImpl = {
            val indent1 = indent + 1

//            log.info(s"-- ${"  " * indent}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
            val resNode: NodeImpl = p match {
//              case c.Alternative      (_)     =>
//              case c.Annotated        (_, _)  =>
//              case c.AppliedTypeTree  (_, _)  =>
//              case c.ApplyDynamic     (_, _)  =>
//              case c.ArrayValue       (_, _)  =>
//              case c.AssignOrNamedArg (_, _)  =>

              case c.Apply(receiver /* :Tree */, args /* :List[Tree] */) =>
                val rcvNode   = parse(receiver, indent1)
                val argNodes  = args.map { child =>
                  parse(child, indent1)
                }
                new ApplyNode(rcvNode, argNodes)

              case c.Assign(lhs /* :Tree */, rhs /* :Tree */) =>
                val lhsNode = parse(lhs, indent1)
                val rhsNode = parse(rhs, indent1)
                new AssignNode(lhsNode, rhsNode)

              case c.Block(init /* :List[Tree] */, last /* :Tree */) =>
                val initNodes = init.map { child =>
                  parse(child, indent1)
                }
                val lastNode  = parse(last, indent1)
                new BlockNode(initNodes, lastNode)

              case c.DefDef(_ /* mods: Modifiers */, _ /* name: TermName */, tParams /* :List[TypeDef] */,
                            vParamsS /* :List[List[ValDef]] */, tpt /* :Tree */, rhs /* :Tree */) =>
                val tParamNodes = tParams.map { child =>
                  complete(child, parseTypeDef(child, indent1))
                }
                val vParamNodesS = vParamsS.map { vParams =>
                  vParams.map { child =>
                    complete(child, parseValDef(child, indent1))
                  }
                }
                val tptNode = parse(tpt, indent1)
                val rhsNode = parse(rhs, indent1)
                new DefDefNode(tParamNodes, vParamNodesS, tptNode, rhsNode)

              case c.EmptyTree =>
                new EmptyNode

              case in: c.Ident => parseIdent(in, indent)

              case c.If(cond /* :Tree */, thenP /* :Tree */, elseP /* :Tree */) =>
                val condNode  = parse(cond , indent1)
                val thenNode  = parse(thenP, indent1)
                val elseNode  = parse(elseP, indent1)
                new IfNode(condNode, thenNode, elseNode)

              case c.LabelDef(_ /* name: TermName */, params /* :List[Ident] */, rhs /* :Tree */) =>
                val paramNodes = params.map { child =>
                  complete(child, parseIdent(child, indent1))
                }
                val rhsNode = parse(rhs, indent1)
                new LabelDefNode(paramNodes, rhsNode)

              case c.Literal(_ /* value: Constant */) =>
                new LiteralNode

              case c.ModuleDef(_ /* mods: Modifiers */, _ /* name: TermName */, child /* :Template */) =>
                val childNode = complete(child, parseTemplate(child, indent1))
                new ModuleDefNode(childNode)

              case c.PackageDef(pid /* :RefTree */, stats /* :List[Tree] */) =>
                val pidNode     = parse(pid, indent1)
                val statNodes   = stats.map { child =>
                  parse(child, indent1)
                }
                new PackageDefNode(pidNode, statNodes)

              case c.Select(child /* :Tree */, _ /* name: Name */) =>
                val childNode = parse(child, indent1)
                new SelectNode(childNode)

              case c.Super(qualifier /* :Tree */, _ /* mix: TypeName */) =>
                val qNode   = parse(qualifier, indent1)
                new SuperNode(qNode)

              case tn: c.Template => parseTemplate(tn, indent)

              case c.This(_ /* qualifier: TypeName */) =>
                new ThisNode

              case td: c.TypeDef => parseTypeDef(td, indent)

              case c.TypeTree() =>
                new TypeTreeNode

              case vd: c.ValDef => parseValDef(vd, indent)

              case _ =>
                log.info(s"-- SKIP ${"  " * indent}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
                new NodeImpl("<unknown>")
            }

            complete(p, resNode)
          }
          val programNode = parse(tree, indent = 0)

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
