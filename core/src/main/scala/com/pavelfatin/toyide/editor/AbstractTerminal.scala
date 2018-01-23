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

import com.pavelfatin.toyide.{ObservableEvents, Interval}

trait AbstractTerminal extends Terminal with ObservableEvents[TerminalEvent] {
  private var _offset     : Int               = 0
  private var _selection  : Option[Interval]  = None
  private var _hover      : Option[Int]       = None
  private var _highlights : Seq[Interval]     = Nil

  def offset: Int = _offset

  def offset_=(i: Int): Unit =
    if (_offset != i) {
      val previous = _offset
      _offset = i
      notifyObservers(CaretMovement(previous, i))
    }

  def selection: Option[Interval] = _selection

  def selection_=(s: Option[Interval]): Unit =
    if (_selection != s) {
      val previous = _selection
      _selection = s
      notifyObservers(SelectionChange(previous, s))
    }

  def hover: Option[Int] = _hover

  def hover_=(i: Option[Int]): Unit =
    if (_hover != i) {
      val previous = _hover
      _hover = i
      notifyObservers(HoverChange(previous, i))
    }

  def highlights: Seq[Interval] = _highlights

  def highlights_=(hs: Seq[Interval]): Unit =
    if (_highlights != hs) {
      val previous = _highlights
      _highlights = hs
      notifyObservers(HighlightsChange(previous, hs))
    }
}