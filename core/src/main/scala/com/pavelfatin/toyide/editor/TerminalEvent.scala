/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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