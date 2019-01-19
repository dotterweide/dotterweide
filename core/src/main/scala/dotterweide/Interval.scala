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
  * @param end    end offset, ''exclusive'' (the interval length is `end - begin`).
  */
case class Interval(begin: Int, end: Int) extends IntervalLike {
  if (begin  < 0) throw new IllegalArgumentException("Begin must be positive: "  + begin )
  if (end    < 0) throw new IllegalArgumentException("End must be positive: "    + end   )
  if (length < 0) throw new IllegalArgumentException("Length must be positive: " + length)

  def intersection(interval: Interval): Interval = {
    val newBegin = math.max(begin, interval.begin)
    val newEnd   = math.max(newBegin, math.min(end, interval.end))
    Interval(newBegin, newEnd)
  }

  /** Adds a delta `n` to the `begin` */
  def withBeginShift(n: Int): Interval = copy(begin = begin + n)

  /** Adds a delta `n` to the `end` */
  def withEndShift  (n: Int): Interval = copy(end   = end   + n)

  /** Shifts the interval forward */
  def + (n: Int) = Interval(begin + n, end + n)

  /** Shifts the interval backward */
  def - (n: Int) = Interval(begin - n, end - n)

  /** Applies function `f` to both `begin` and `end` */
  def transformWith(f: Int => Int): Interval = copy(begin = f(begin), end = f(end))
}