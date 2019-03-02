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

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import dotterweide.document.Document
import dotterweide.editor.{Adviser, Async, Data}
import dotterweide.languages.scala.CompilerActor._
import dotterweide.lexer.Token
import dotterweide.node.{Node, NodeType}
import dotterweide.parser.Parser

import scala.concurrent.Future
import scala.util.{Failure, Success}

/** The parser for the Scala language runs a compiler instance in an Akka actor
  * (thus single threaded) which may be queried for nodes.
  *
  * The caller should make sure not to clutter the mailbox.
  * After each compilation, the `Global#Tree` is converted to a `ScalaTree` tree.
  * This allows us to limit the dependency on the Scala compiler API and to evolve
  * the tree structure according to our needs. Not all tree types are mapped yet,
  * but everything covered in the Dotterweide code-base itself is mapped.
  */
class ScalaParser(scalaVersion: String, prelude: String, postlude: String) extends Parser with Adviser {

  private[this] final val DEBUG = false

  private[this] val system = if (!DEBUG) ActorSystem("ScalaParser") else {
    ActorSystem("ScalaParser",
      ConfigFactory.parseString("""akka.loglevel = DEBUG""")  // WTF, is there no simpler way to set the log level?
    )
  }

  private[this] val compilerActor: ActorRef = system.actorOf(Props(
    new CompilerActor(scalaVersion = scalaVersion, prelude = prelude, postlude = postlude)
  ), "compiler")

  def parseAsync(text: String, tokens: Iterator[Token])(implicit async: Async): Future[Node] = {
    import async.executionContext

    // XXX TODO --- timeout is arbitrary
    val fut = compilerActor.ask(Compile(text))(Timeout(5, TimeUnit.SECONDS)).mapTo[Node]
    fut.onComplete {
      case Success(_) =>
      case Failure(ex) =>
        println("Compilation failed:")
        ex.printStackTrace()
    }
    fut
  }

  def typeAtAsync(document: Document, data: Data, offset: Int)
                 (implicit async: Async): Future[Option[NodeType]] = {
    import async.executionContext

    // XXX TODO --- timeout is arbitrary
    val fut = compilerActor.ask(Type(document.text, offset))(Timeout(5, TimeUnit.SECONDS)).mapTo[Option[NodeType]]
    fut.onComplete {
      case Success(_) =>
      case Failure(ex) =>
        println("Type failed:")
        ex.printStackTrace()
    }
    fut
  }

  def variantsAsync(document: Document, data: Data, offset: Int)
                   (implicit async: Async): Future[Adviser.Result] = {
    import async.executionContext

    // XXX TODO --- timeout is arbitrary
    val fut = compilerActor.ask(Complete(document.text, offset))(Timeout(5, TimeUnit.SECONDS)).mapTo[Adviser.Result]
    fut.onComplete {
      case Success(_) =>
      case Failure(ex) =>
        println("Completion failed:")
        ex.printStackTrace()
    }
    fut
  }

  def dispose(): Unit = {
    system.terminate()  // or `stop(compileActor)`?
  }
}
