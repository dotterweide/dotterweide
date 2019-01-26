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

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}

import scala.collection.immutable.{Seq => ISeq}

/** TODO: implementation as in IntelliJ:
  *
  * has-selection?
  *  - yes. do all lines in selection start with line comment?
  *     - yes. then remove line comments in selection; leave selection and cursor in place
  *     - no. then add line comments in selection; leave selection and cursor in place
  *
  *  - no. does current line start with line comment?
  *      - yes. then remove line comment and move cursor to next line.
  *      - no. then add line comment. if line was empty, place cursor after comment chars, otherwise
  *        move cursor to next line.
  */
private class ToggleLineComment(document: Document, terminal: Terminal, comment: String) extends Action {
  def name: String        = "Toggle Line Comment"
  def mnemonic: Char      = 'T'
  def keys: ISeq[String]  = "ctrl pressed SLASH" :: Nil

  def apply(): Unit = {
    val oldSelection  = terminal.selection
    val lineNumbers   = oldSelection match {
      case Some(sel) =>
        val beginLine = document.lineNumberOf(sel.begin)
        val endLine0  = document.lineNumberOf(sel.end  )
        // don't include last line if selection ends on beginning of line
        val endIsBOL  = document.startOffsetOf(endLine0) == sel.end
        val endLine   = if (endIsBOL) endLine0 - 1 else endLine0

        beginLine to endLine

      case None =>
        document.lineNumberOf(terminal.offset) :: Nil
    }

    val tuples = lineNumbers.map { lineNumber =>
      val interval  = document.intervalOfNl(lineNumber)
      val line      = document.text(interval)
      (interval, line)
    }

    val hasSelection      = oldSelection.isDefined
    val addComment        = !tuples.forall(_._2.trim.startsWith(comment))
    val csrAfterSelBegin  = oldSelection.exists(_.begin < terminal.offset)
    val moveCursor        = !hasSelection

    terminal.selection = None
    // make sure we go backwards, so intervals are valid during editing!
    tuples.reverseIterator.foreach { case (interval, line) =>
      if (addComment) {
        document.insert(interval.begin, comment)
        if (moveCursor && terminal.offset > interval.begin) terminal.offset += comment.length
      } else {
        val i = interval.begin + line.indexOf(comment)
        val commentInterval = Interval(i, i + comment.length)
        document.remove(commentInterval)
        if (moveCursor) {
          if (commentInterval.touches(terminal.offset)) {
            terminal.offset = i
          } else {
            terminal.offset -= comment.length
          }
        }
      }
    }

    if (hasSelection) {    // restore selection
      val selBegin        = document.startOffsetOf    (lineNumbers.head)
      val selEnd          = document.startOffsetOfClip(lineNumbers.last + 1)
      terminal.selection  = Some(Interval(selBegin, selEnd))
      terminal.offset     = if (csrAfterSelBegin) selEnd else selBegin

    } else {
      val lineWasEmpty = tuples.headOption.forall(_._2.trim.isEmpty)
      if (addComment && lineWasEmpty) {
        // we move beyond the comment
        terminal.offset = document.endOffsetOf(terminal.currentLineNumberIn(document))
      } else {
        // when we uncomment or we comment and the line was not empty, we move down
        moveCaretDown()
      }
    }
  }

  private def moveCaretDown(): Unit =
    if (document.lineNumberOf(terminal.offset) < document.linesCount - 1) {
      val line        = terminal.currentLineNumberIn(document)
      val indent      = terminal.offset - document.startOffsetOf(line)
      val target      = document.startOffsetOf(line + 1) + indent
      terminal.offset = target.min(document.endOffsetOf(line + 1))
    }
}