/*
 *  Interval.scala
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

/** An integer range (in a text).
  *
  * @param begin  start offset, zero based, inclusive
  * @param end    end offset, _exclusive_ (the interval length is `end - begin`).
  */
case class Interval(begin: Int, end: Int) extends IntervalLike {
  if (begin  < 0) throw new IllegalArgumentException("Begin must be positive: "  + begin )
  if (end    < 0) throw new IllegalArgumentException("End must be positive: "    + end   )
  if (length < 0) throw new IllegalArgumentException("Length must be positive: " + length)

  def intersection(interval: Interval): Interval = {
    val from = begin.max(interval.begin)
    Interval(from, from.max(end.min(interval.end)))
  }

  def withBeginShift(n: Int): Interval = copy(begin = begin + n)
  def withEndShift  (n: Int): Interval = copy(end   = end   + n)

  def +(n: Int) = Interval(begin + n, end + n)
  def -(n: Int) = Interval(begin - n, end - n)

  def transformWith(f: Int => Int): Interval = copy(begin = f(begin), end = f(end))
}