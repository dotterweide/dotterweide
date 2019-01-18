/*
 *  TerminalEvent.scala
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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.Interval

sealed trait TerminalEvent {
  def undo(terminal: Terminal): Unit

  def redo(terminal: Terminal): Unit
}

case class CaretMovement(from: Int, to: Int) extends TerminalEvent {
  def undo(terminal: Terminal): Unit = {
    terminal.offset = from
  }

  def redo(terminal: Terminal): Unit = {
    terminal.offset = to
  }
}

case class SelectionChange(from: Option[Interval], to: Option[Interval]) extends TerminalEvent {
  def undo(terminal: Terminal): Unit = {
    terminal.selection = from
  }

  def redo(terminal: Terminal): Unit = {
    terminal.selection = to
  }
}

case class HighlightsChange(from: Seq[Interval], to: Seq[Interval]) extends TerminalEvent {
  def undo(terminal: Terminal): Unit = {
    terminal.highlights = from
  }

  def redo(terminal: Terminal): Unit = {
    terminal.highlights = to
  }
}

case class HoverChange(from: Option[Int], to: Option[Int]) extends TerminalEvent {
  def undo(terminal: Terminal): Unit = {
    terminal.hover = from
  }

  def redo(terminal: Terminal): Unit = {
    terminal.hover = to
  }
}