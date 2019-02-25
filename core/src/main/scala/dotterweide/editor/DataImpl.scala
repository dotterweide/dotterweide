/*
 *  DataImpl.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

/*
 * Original code copyright 2018 Pavel Fatin, https://pavelfatin.com
 * Licensed under the Apache License, Version 2.0 (the "License"): http://www.apache.org/licenses/LICENSE-2.0
 */

package dotterweide.editor

import dotterweide.document.Document
import dotterweide.inspection.{Inspection, Mark}
import dotterweide.lexer.{Lexer, Token}
import dotterweide.node.Node
import dotterweide.parser.{Parser, SyncParser}

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}

/** An implementation of `Data` that re-runs the initial text pass whenever the `document` changes. */
private class DataImpl(document: Document, lexer: Lexer, parser: Parser, inspections: ISeq[Inspection])
                      (implicit async: Async) extends Data {
  def text: String = document.text

  var tokens    : ISeq[Token]   = Nil
  var structure : Option[Node]  = None
  var errors    : ISeq[Error]   = Nil

  var hasFatalErrors: Boolean = errors.exists(_.fatal)

  var pass: Pass = Pass.Text

  document.onChange { _ =>
    runTextPass()
  }

  def hasNextPass: Boolean = pass.next.isDefined

  private[this] var busyCount     = 0
  private[this] var busyPass      = -1

  private[this] var futStructure: Future[Option[Node]] = Future.successful(None)

  def nextPass(): Unit = {
    val next = pass.next.getOrElse(
      throw new IllegalStateException("Next pass is unavailable"))

    if (busyPass < 0) {
      pass = next
      runPass()
    }
  }

//  def compute(): Unit =
//    while (hasNextPass) {
//      nextPass()
//    }

  def computeStructure(): Future[Option[Node]] = {
    import Ordering.Implicits._

    while (pass < Pass.Parser) {
      require (busyPass < 0)
      pass = pass.next.get
      runPass()
    }
    futStructure
  }

  private def runPass(): Unit = {
    pass match {
      case Pass.Parser  => runParserPass()
      case _            => runSync()
    }
  }

  /** Runs a given synchronous pass and adds new errors to previous errors. */
  private def runSync(): Unit = {
    val passErrors = pass match {
      case Pass.Lexer       => runLexerPass()
      case Pass.Inspections => runInspectionPass()
      case _                => throw new IllegalStateException(s"runSync $pass")
    }
    passCompleted(passErrors)
  }

  private def passCompleted(passErrors: ISeq[Error]): Unit = {
    errors         ++= passErrors
    hasFatalErrors ||= passErrors.exists(_.fatal)

    notifyObservers(DataEvent(pass, passErrors))
  }

  /** Resets all analysis data. Clears all errors. */
  private def runTextPass(): Unit = {
    pass            = Pass.Text
    tokens          = Nil
    structure       = None
    errors          = Nil
    hasFatalErrors  = false
    busyPass        = -1

    notifyObservers(DataEvent(pass, Nil))
  }

  /** Generates `tokens`. Returns the resulting errors. */
  private def runLexerPass(): ISeq[Error] = {
    tokens = lexer.analyze(document.characters).toStream

    tokens.collect {
      case Token(_, span, Some(message)) => Error(span.interval, message)
    }
  }

  /** Generates `structure`. */
  private def runParserPass(): Unit = {
    val it = tokens.iterator
    parser match {
      case ps: SyncParser =>
        val root: Node = ps.parse(it)
        parserCompleted(root)
        futStructure = Future.successful(structure)

      case _ =>
        require (busyPass < 0)
        busyCount += 1
        val id     = busyCount
        val fut: Future[Node] = parser.parseAsync(text, it)
        busyPass   = id

        import async.executionContext
        val pr = Promise[Option[Node]]()
        fut.onComplete { tr =>
          async.defer {
            val sTr: Try[Option[Node]] = tr.flatMap { root =>
              if (busyPass == id) {
                busyPass = -1
                parserCompleted(root)
                Success(structure)
              } else {
                Failure(Aborted())
              }
            }
            pr.tryComplete(sTr)
          }
        }
        futStructure = pr.future
    }
  }

  private def parserCompleted(root: Node): Unit = {
    structure = Some(root)
    val passErrors = root.elements.map(node => (node, node.problem)).collect {
      case (node, Some(message)) => Error(node.span.interval, message)
    }
    passCompleted(passErrors)
  }

  /** Generates and returns more errors and warnings through inspection. */
  private def runInspectionPass(): ISeq[Error] = {
    val root = structure.getOrElse(
      throw new IllegalStateException("Running inspections prior to parser"))

    root.elements.flatMap(node => inspections.flatMap(_.inspect(node))).collect {
      case Mark(node, message, decoration, warning) => Error(node.span.interval, message, decoration, !warning)
    }
  }
}
