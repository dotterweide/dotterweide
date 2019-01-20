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
import dotterweide.languages.scala.node.{Block, PackageNode}
import dotterweide.lexer.Token
import dotterweide.node.Node
import dotterweide.parser.Parser

import scala.concurrent.Future
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

    def receive: Receive = {
      case Compile(text) =>
        val reply = try {
          log.info("begin compile")
          val tree: c.Tree = compile(text)
          log.info("done compile")

          def loop(p: Global#Tree, indent: Int): Unit = {
            val indent1 = indent + 1
            log.info(s"-- ${"  " * indent}${p.productPrefix} | ${p.pos} ${p.pos.getClass.getSimpleName}")
            p match {
//              case c.Alternative      (_)     =>
//              case c.Annotated        (_, _)  =>
//              case c.AppliedTypeTree  (_, _)  =>
//              case c.ApplyDynamic     (_, _)  =>
//              case c.ArrayValue       (_, _)  =>
//              case c.AssignOrNamedArg (_, _)  =>

              case c.Apply(receiver /* :Tree */, args /* :List[Tree] */) =>
                loop(receiver, indent1)
                args.foreach { child =>
                  loop(child, indent1)
                }

              case c.Assign(lhs /* :Tree */, rhs /* :Tree */) =>
                loop(lhs, indent1)
                loop(rhs, indent1)

              case c.Block(init /* :List[Tree] */, last /* :Tree */) =>
                init.foreach { child =>
                  loop(child, indent1)
                }
                loop(last, indent1)

              case c.DefDef(_ /* mods: Modifiers */, _ /* name: TermName */, tParams /* :List[TypeDef] */,
                            vParamsS /* :List[List[ValDef]] */, tpt /* :Tree */, rhs /* :Tree */) =>
                tParams.foreach { child =>
                  loop(child, indent1)
                }
                vParamsS.foreach { vParams =>
                  vParams.foreach { child =>
                    loop(child, indent1)
                  }
                }
                loop(tpt, indent1)
                loop(rhs, indent1)

              case c.EmptyTree =>

              case c.If(cond /* :Tree */, thenP /* :Tree */, elseP /* :Tree */) =>
                loop(cond , indent1)
                loop(thenP, indent1)
                loop(elseP, indent1)

              case c.Ident(_ /* name: Name */) =>

              case c.LabelDef(_ /* name: TermName */, params /* :List[Ident] */, rhs /* :Tree */) =>
                params.foreach { child =>
                  loop(child, indent1)
                }
                loop(rhs, indent1)

              case c.Literal(_ /* value: Constant */) =>

              case c.ModuleDef(_ /* mods: Modifiers */, _ /* name: TermName */, child /* :Template */) =>
                loop(child, indent1)

              case c.PackageDef(pid /* :RefTree */, stats /* :List[Tree] */) =>
                loop(pid, indent1)
                stats.foreach { child =>
                  loop(child, indent1)
                }

              case c.Select(child /* :Tree */, _ /* name: Name */) =>
                loop(child, indent1)

              case c.Super(qualifier /* :Tree */, _ /* mix: TypeName */) =>
                loop(qualifier, indent1)

              case c.Template(parents /* :List[Tree] */, valDef /* :ValDef */, stats /* :List[Tree] */) =>
                parents.foreach { child =>
                  loop(child, indent1)
                }
                loop(valDef , indent1)
                stats.foreach { child =>
                  loop(child, indent1)
                }

              case c.This(_ /* qualifier: TypeName */) =>

              case c.TypeTree() =>

              case c.ValDef(_ /* mods: Modifiers */, _ /* name: TermName */, tpt /* :Tree */, rhs /* :Tree */) =>
                loop(tpt, indent1)
                loop(rhs, indent1)

              case _ =>
            }
          }
          loop(tree, indent = 0)

          val n = new PackageNode()
          n.children = reporter.infos.iterator.filter(info => info.pos.isDefined && info.severity.id >= 2).map { info =>
            val child     = new Block()
            import info.pos._
            log.info(s"info [$start, $end]")
            child.span    = Span("", start, end)
            child.problem = Some(info.msg)
            child
          } .toVector // .sortBy(_.span.begin)
          n
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
