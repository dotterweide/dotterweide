/*
 *  Span.scala
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

package dotterweide

/** A text span combines a text substring with its interval within a parent text.
  *
  * @param source     the (entire) parent text
  * @param interval   the interval to select a substring
  */
case class Span(source: CharSequence, interval: Interval) extends IntervalLike {
  def start : Int = interval.start
  def stop   : Int = interval.stop

  /** The text denoted by `source.subSequence(begin, end)`. */
  def text: String = source.subSequence(start, stop).toString

  def leftEdge: Span = Span(source, start, start)

  override def toString: String = s"$productPrefix(..., $interval)"
}

object Span {
  def apply(source: CharSequence, begin: Int, end: Int): Span = Span(source, Interval(begin, end))
}