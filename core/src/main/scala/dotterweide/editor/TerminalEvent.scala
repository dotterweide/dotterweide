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

package dotterweide.editor

import dotterweide.Interval

import scala.collection.immutable.{Seq => ISeq}

sealed trait TerminalEvent extends UndoableEdit {
  def terminal: Terminal

  def significant: Boolean = false
}

case class CaretMovement(terminal: Terminal, before: Int, now: Int) extends TerminalEvent {
  def undo(): Unit =
    terminal.offset = before

  def redo(): Unit =
    terminal.offset = now

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = succ match {
    case that: CaretMovement if this.terminal == that.terminal =>
      val m = copy(now = that.now)
      Some(m)

    case _ => None
  }
}

case class CaretMode(terminal: Terminal, overwrite: Boolean) extends TerminalEvent {
  def undo(): Unit =
    terminal.overwriteMode = !overwrite

  def redo(): Unit =
    terminal.overwriteMode = overwrite

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = None
}

case class SelectionChange(terminal: Terminal, before: Option[Interval], now: Option[Interval]) extends TerminalEvent {
  def undo(): Unit =
    terminal.selection = before

  def redo(): Unit =
    terminal.selection = now

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = succ match {
    case that: SelectionChange if this.terminal == that.terminal =>
      val m = copy(now = that.now)
      Some(m)

    case _ => None
  }
}

case class HighlightsChange(terminal: Terminal, before: ISeq[Interval], now: ISeq[Interval]) extends TerminalEvent {
  def undo(): Unit =
    terminal.highlights = before

  def redo(): Unit =
    terminal.highlights = now

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = succ match {
    case that: HighlightsChange if this.terminal == that.terminal =>
      val m = copy(now = that.now)
      Some(m)

    case _ => None
  }
}

case class HoverChange(terminal: Terminal, before: Option[Int], now: Option[Int]) extends TerminalEvent {
  def undo(): Unit =
    terminal.hover = before

  def redo(): Unit =
    terminal.hover = now

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = succ match {
    case that: HoverChange if this.terminal == that.terminal =>
      val m = copy(now = that.now)
      Some(m)

    case _ => None
  }
}