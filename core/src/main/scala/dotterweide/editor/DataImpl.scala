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
import dotterweide.parser.Parser

import scala.collection.immutable.{Seq => ISeq}

private class DataImpl(document: Document, lexer: Lexer, parser: Parser, inspections: ISeq[Inspection]) extends Data {
  def text: String = document.text

  var tokens    : ISeq[Token]   = Nil
  var structure : Option[Node]  = None
  var errors    : ISeq[Error]   = Nil

  var hasFatalErrors: Boolean = errors.exists(_.fatal)

  var pass: Pass = Pass.Text

  document.onChange { _ =>
    run(Pass.Text)
  }

  def hasNextPass: Boolean = pass.next.isDefined

  def nextPass(): Unit = {
    val next = pass.next.getOrElse(
      throw new IllegalStateException("Next pass is unavailable"))

    run(next)
  }

  def compute(): Unit =
    while (hasNextPass) {
      nextPass()
    }

  private def run(p: Pass): Unit = {
    pass = p

    val passErrors = p match {
      case Pass.Text        => runTextPass()
      case Pass.Lexer       => runLexerPass()
      case Pass.Parser      => runParserPass()
      case Pass.Inspections => runInspectionPass()
    }

    errors ++= passErrors
    hasFatalErrors = hasFatalErrors || passErrors.exists(_.fatal)

    notifyObservers(DataEvent(pass, passErrors))
  }

  private def runTextPass(): ISeq[Error] = {
    tokens          = Nil
    structure       = None
    errors          = Nil
    hasFatalErrors  = false

    Nil
  }

  private def runLexerPass(): ISeq[Error] = {
    tokens = lexer.analyze(document.characters).toStream

    tokens.collect {
      case Token(_, span, Some(message)) => Error(span.interval, message)
    }
  }

  private def runParserPass(): ISeq[Error] = {
    val root = parser.parse(tokens.iterator)

    structure = Some(root)

    root.elements.map(node => (node, node.problem)).collect {
      case (node, Some(message)) => Error(node.span.interval, message)
    }
  }

  private def runInspectionPass(): ISeq[Error] = {
    val root = structure.getOrElse(
      throw new IllegalStateException("Running inspections prior to parser"))

    root.elements.flatMap(node => inspections.flatMap(_.inspect(node))).collect {
      case Mark(node, message, decoration, warning) => Error(node.span.interval, message, decoration, !warning)
    }
  }
}
