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

class ScalaParser extends Parser {
  private val system                  = ActorSystem("ScalaParser")
  private val compilerActor: ActorRef = system.actorOf(Props(new CompilerActor), "compiler")

  private case class Compile(text: String)

  private class CompilerActor extends Actor {
    private val log = Logging(context.system, this)

    private val reporter = new StoreReporter

    private lazy val compiler: Global = {
      val outputDir = new VirtualDirectory("<virtual-dir>", None)
      val settings  = new Settings(err => Console.err.println(err))

      settings.outputDirs.setSingleOutput(outputDir)
      settings.usejavacp.value              = true
      settings.YpresentationAnyThread.value = true // needed to print typed tree
      settings.source.value                 = ScalaVersion("2.12.8")

      new Global(settings, reporter)
    }

    private def compile(text: String): compiler.Tree = {
      import compiler._
      val srcFile   = newSourceFile(text)
      val respTypes = new Response[compiler.Tree]
      compiler.askReset()
      compiler.askLoadedTyped(srcFile, keepLoaded = false /* true */, respTypes)
      val treeTyped: compiler.Tree = respTypes.get.left.get
      treeTyped
    }

    def receive: Receive = {
      case Compile(text) =>
        val reply = try {
          log.debug("begin compile")
          /* val tree = */ compile(text)
          log.debug("done compile")
          val n = new PackageNode()
          n.children = reporter.infos.iterator.filter(info => info.pos.isDefined && info.severity.id >= 2).map { info =>
            val child     = new Block()
            import info.pos._
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
