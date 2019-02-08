/*
 *  AbstractTerminal.scala
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

import dotterweide.{Interval, ObservableEvents}

import scala.collection.immutable.{Seq => ISeq}

trait AbstractTerminal extends Terminal with ObservableEvents[TerminalEvent] {
  private var _offset     : Int               = 0
  private var _selection  : Option[Interval]  = None
  private var _hover      : Option[Int]       = None
  private var _highlights : ISeq[Interval]    = Nil

  def offset: Int = _offset

  def offset_=(i: Int): Unit =
    if (_offset != i) {
      val previous = _offset
      _offset = i
      notifyObservers(CaretMovement(this, before = previous, now = i))
    }

  def selection: Option[Interval] = _selection

  def selection_=(s: Option[Interval]): Unit =
    if (_selection != s) {
      val previous = _selection
      _selection = s
      notifyObservers(SelectionChange(this, before = previous, now = s))
    }

  def hover: Option[Int] = _hover

  def hover_=(i: Option[Int]): Unit =
    if (_hover != i) {
      val previous = _hover
      _hover = i
      notifyObservers(HoverChange(this, before = previous, now = i))
    }

  def highlights: ISeq[Interval] = _highlights

  def highlights_=(hs: ISeq[Interval]): Unit =
    if (_highlights != hs) {
      val previous = _highlights
      _highlights = hs
      notifyObservers(HighlightsChange(this, before = previous, now = hs))
    }
}