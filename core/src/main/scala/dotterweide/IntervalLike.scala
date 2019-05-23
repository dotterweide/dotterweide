/*
 *  IntervalLike.scala
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

trait IntervalLike {
  /** Start offset, zero based, inclusive */
  def start: Int

  /** End offset, _exclusive_ */
  def stop: Int

  /** The interval length is `end - begin` */
  def length: Int = stop - start

  def isEmpty : Boolean = start == stop
  def nonEmpty: Boolean = start <  stop

  def includes(offset: Int): Boolean = start <= offset && offset < stop
  def touches (offset: Int): Boolean = start <= offset && offset <= stop

  def touchesNonEmpty(offset: Int): Boolean = touches(offset) && nonEmpty

  def includes(that: IntervalLike): Boolean =
    (this.nonEmpty && that.nonEmpty) &&
      that.start >= this.start && that.stop <= this.stop

  def matches(that: IntervalLike): Boolean =
    (this.nonEmpty && that.nonEmpty) &&
      that.start == this.start && that.stop == this.stop

  def intersectsWith(that: IntervalLike): Boolean =
    (this.nonEmpty && that.nonEmpty) &&
      (this.includes(that.start) || this.includes(that.stop - 1) ||
       that.includes(this.start) || that.includes(this.stop - 1))

  def overlaps(that: IntervalLike): Boolean =
    (this.nonEmpty && that.nonEmpty) &&
      math.max(this.start, that.start) < math.min(this.stop, that.stop)
}
