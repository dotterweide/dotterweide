/*
 *  CompilerActor.scala
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

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.DotterweidePeek._
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion

private object CompilerActor {
  case class Compile  (text: String)
  case class Complete (text: String, offset: Int)
  case class Type     (text: String, offset: Int)
}
private class CompilerActor(scalaVersion: String, protected val prelude: String, protected val postlude: String)
  extends Actor with ParserImpl with AdviserImpl with TypeImpl with AbstractCompilerActor {

  import CompilerActor._

  protected val log: LoggingAdapter = Logging(context.system, this)

  protected val reporter: StoreReporter = new StoreReporter

  protected val c: Global = {
    val outputDir = new VirtualDirectory("<virtual-dir>", None)
    val settings  = new Settings(err => Console.err.println(err))

    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp              .value  = true

    // Message from Jason Zaugg: When using `YpresentationAnyThread`,
    // we can go directly into the API without using the `ask` methods,
    // if we ensure we're single threaded -- which is the case inside the actor.
    // When setting that setting to `false`, we have to go through `ask`
    // methods and wait for the responses to complete. `ask` is incompatible
    // with a number of things, including `completionsAt`.

    settings.YpresentationAnyThread .value  = true
    settings.Yrangepos              .value  = true
    //      settings.Yvalidatepos           .value  = "analyze" :: Nil
    settings.source                 .value  = ScalaVersion(scalaVersion) // "2.12.8"

    val res = new Global(settings, reporter)
    // we simply set this scheduler as an insurance that we do not accidentally schedule requests
    scheduler_=(res)(new res.NoWorkScheduler)
    res
  }

  // Executes `body` and returns its result to the sender.
  // If an exception occurs, sends that exception back as `Status.Failure`
  private def tryHandle(cmd: String)(body: => Any): Unit = {
    val reply: Any = try {
      log.debug(s"begin $cmd")
      val res = body
      log.debug(s"done $cmd")
      res
    } catch {
      case e: Exception => akka.actor.Status.Failure(e)
    }
    sender() ! reply
  }

  def receive: Receive = {
    case Compile  (text)          => tryHandle("compile"  )(runCompile  (text))
    case Complete (text, offset)  => tryHandle("complete" )(runComplete (text, offset))
    case Type     (text, offset)  => tryHandle("type"     )(runType     (text, offset))

    case m =>
      log.error(s"Unknown message $m")
  }
}
