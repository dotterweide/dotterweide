/*
 *  LinesHolder.scala
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

package dotterweide.document

import dotterweide.Interval

import scala.collection.immutable.{Seq => ISeq}

trait LinesHolder {
  def length: Int

  protected def wraps: ISeq[Int]

  def linesCount: Int = wraps.size + 1

  def lineNumberOf(offset: Int): Int = {
    if (offset < 0 || offset > length) throw new IndexOutOfBoundsException()
    wraps.view.takeWhile(_ < offset).size
  }

  def startOffsetOf(line: Int): Int = {
    if (line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    if (line == 0) 0 else wraps(line - 1) + 1
  }

  def endOffsetOf(line: Int): Int = {
    if (line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    if (line == wraps.size) length else wraps(line)
  }

  /** Like `startOffsetOf`, but if line is too small, clips to zero, if line is too large, returns `length`. */
  def startOffsetOfClip(line: Int): Int =
    if      (line <= 0) 0
    else if (line >= linesCount) length
    else wraps(line - 1) + 1

  /** Interval of a given line, not including newline. */
  def intervalOf(line: Int): Interval = {
    if (line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    Interval(startOffsetOf(line), endOffsetOf(line))
  }

  /** Line `intervalOf` but including newline if the line is not the last line. */
  def intervalOfNl(line: Int): Interval = {
    if (line < 0 || line >= linesCount) throw new IndexOutOfBoundsException()
    Interval(startOffsetOf(line), startOffsetOfClip(line + 1))
  }

  def toLocation(offset: Int): Location = {
    if (offset < 0 || offset > length) throw new IndexOutOfBoundsException()
    val line = lineNumberOf(offset)
    Location(line, offset - startOffsetOf(line))
  }

  def toOffset(location: Location): Option[Int] = {
    if (location.line >= linesCount) return None
    val offset = startOffsetOf(location.line) + location.indent
    if (offset <= endOffsetOf(location.line)) Some(offset) else None
  }

  def toNearestOffset(location: Location): Int = {
    val line = location.line.min(linesCount - 1)
    val offset = startOffsetOf(line) + location.indent
    offset.min(endOffsetOf(line))
  }

  def maximumIndent: Int =
    Range(0, linesCount).view.map(line => toLocation(endOffsetOf(line)).indent).max
}

case class Location(line: Int, indent: Int)