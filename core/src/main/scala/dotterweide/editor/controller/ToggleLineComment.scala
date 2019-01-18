/*
 *  ToggleLineComment.scala
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

import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}
import dotterweide.Interval

private class ToggleLineComment(document: Document, terminal: Terminal, comment: String) extends Action {
  def keys: Seq[String] = List("ctrl pressed SLASH")

  def apply(): Unit = {
    val interval  = terminal.currentLineIntervalIn(document)
    val line      = document.text(interval)

    terminal.selection = None

    if (line.trim.startsWith(comment)) {
      val i = interval.begin + line.indexOf(comment)
      val commentInterval = Interval(i, i + comment.length)
      document.remove(commentInterval)
      if (commentInterval.touches(terminal.offset))
        terminal.offset = i
      else
        terminal.offset -= comment.length
    } else {
      document.insert(interval.begin, comment)
      if (terminal.offset > interval.begin) terminal.offset += comment.length
    }

    moveCaretDown()
  }

  private def moveCaretDown(): Unit =
    if (document.lineNumberOf(terminal.offset) < document.linesCount - 1) {
      val line = document.lineNumberOf(terminal.offset)
      val indent = terminal.offset - document.startOffsetOf(line)
      val target = document.startOffsetOf(line + 1) + indent
      terminal.offset = target.min(document.endOffsetOf(line + 1))
      terminal.selection = None
    }
}