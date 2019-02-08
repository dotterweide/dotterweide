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
  * @param start  start offset, zero based, inclusive
  * @param stop    end offset, ''exclusive'' (the interval length is `end - begin`).
  */
case class Interval(start: Int, stop: Int) extends IntervalLike {
  if (start  < 0) throw new IllegalArgumentException("Begin must be positive: "  + start )
  if (stop    < 0) throw new IllegalArgumentException("End must be positive: "    + stop   )
  if (length < 0) throw new IllegalArgumentException("Length must be positive: " + length)

  def intersection(interval: Interval): Interval = {
    val newBegin = math.max(start, interval.start)
    val newEnd   = math.max(newBegin, math.min(stop, interval.stop))
    Interval(newBegin, newEnd)
  }

  /** Adds a delta `n` to the `begin` */
  def withBeginShift(n: Int): Interval = copy(start = start + n)

  /** Adds a delta `n` to the `end` */
  def withEndShift  (n: Int): Interval = copy(stop   = stop   + n)

  /** Shifts the interval forward */
  def + (n: Int) = Interval(start + n, stop + n)

  /** Shifts the interval backward */
  def - (n: Int) = Interval(start - n, stop - n)

  /** Applies function `f` to both `begin` and `end` */
  def transformWith(f: Int => Int): Interval = copy(start = f(start), stop = f(stop))
}