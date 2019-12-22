/*
 *  UnindentSelection.scala
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

package dotterweide.editor.controller

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.editor.Terminal

import scala.collection.immutable.{Seq => ISeq}

private class UnindentSelection(document: Document, terminal: Terminal, tabSize: Int)
  extends DocumentAction(document, terminal) {

  def name: String        = "Unindent Selection"
  def mnemonic: Char      = 'N'
  val keys: ISeq[String]  = "shift pressed TAB" :: Nil

  protected def calcEnabled(): Boolean = true

  private def apply(beginLine: Int, endLine: Int, moveCursor: Boolean): Unit = {
    val interval  = Interval(document.startOffsetOf(beginLine), document.endOffsetOf(endLine))
    val text      = document.text(interval)

    val replacement = text.split("\n").map(s =>
      s.drop(math.min(tabSize, s.takeWhile(_.isWhitespace).length))
    ).mkString("\n")

    val decrement = text.length - replacement.length
    if (moveCursor) terminal.offset -= decrement
    terminal.selection.foreach { iv =>
      terminal.selection = Some(iv.withEndShift(-decrement))
    }

    document.replace(interval, replacement)
  }

  def apply(): Unit =
    terminal.selection match {
      case Some(iv) =>
        val selection   = if (document.toLocation(iv.stop).indent == 0) iv.withEndShift(-1) else iv
        val beginLine   = document.lineNumberOf(selection.start)
        val endLine     = document.lineNumberOf(selection.stop)
        apply(beginLine, endLine, moveCursor = terminal.offset > iv.start)


      case None =>
        val line = document.lineNumberOf(terminal.offset)
        apply(line, line, moveCursor = true)
    }
}