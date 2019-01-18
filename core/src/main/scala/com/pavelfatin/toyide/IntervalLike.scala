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

package com.pavelfatin.toyide

trait IntervalLike {
  /** Start offset, zero based, inclusive */
  def begin : Int
  /** End offset, _exclusive_ */
  def end   : Int

  /** The interval length is `end - begin` */
  def length: Int = end - begin

  def empty: Boolean = length == 0

  def includes(offset: Int): Boolean = begin <= offset && offset < end
  def touches (offset: Int): Boolean = begin <= offset && offset <= end

  def includes(interval: IntervalLike): Boolean =
    (!empty && !interval.empty) &&
      interval.begin >= begin && interval.end <= end

  def intersectsWith(interval: IntervalLike): Boolean =
    (!empty && !interval.empty) &&
      (includes(interval.begin) || includes(interval.end - 1) ||
        interval.includes(begin) || interval.includes(end - 1))
}
