/*
 *  Document.scala
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

package com.pavelfatin.toyide.document

import com.pavelfatin.toyide.{ObservableEvents, Interval}

trait Document extends LinesHolder with ObservableEvents[DocumentEvent] {
  var text: String

  def text(begin: Int, end: Int): String = characters.subSequence(begin, end).toString

  def text(interval: Interval): String = text(interval.begin, interval.end)

  def characters: CharSequence

  def charAt(offset: Int): Char = characters.charAt(offset)

  def charOptionAt(offset: Int): Option[Char] =
    if (offset >= 0 && offset < length) Some(charAt(offset)) else None

  def insert(offset: Int, s: String): Unit

  def remove(begin: Int, end: Int): Unit

  def remove(interval: Interval): Unit =
    remove(interval.begin, interval.end)

  def replace(begin: Int, end: Int, s: String): Unit

  def replace(interval: Interval, s: String): Unit =
    replace(interval.begin, interval.end, s)

  def createAnchorAt(offset: Int, bias: Bias): Anchor
}

sealed trait Bias

object Bias {
  case object Left  extends Bias
  case object Right extends Bias
}
