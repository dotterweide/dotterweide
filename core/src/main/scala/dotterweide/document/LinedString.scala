/*
 *  LinedString.scala
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

import dotterweide.document.LinedString._

import scala.collection.immutable.{Seq => ISeq}

// XXX TODO --- very inefficient to traverse a list of lines to find a char index
private class LinedString private (val lines: List[CharSequence]) extends CharSequence {
  def this(s: String) {
    this(parseLines(s))
  }

  // ---- CharSequence interface ----

  lazy val length: Int = lines.foldLeft(0)(_ + _.length)

  def charAt(index: Int): Char = charAt(index, lines)

  def subSequence(start: Int, end: Int) = new LinedString(subLines(start, end))

  override lazy val toString: String = lines.foldLeft(new StringBuilder())(_ append _).toString()

  // ----

  def concat(other: LinedString): LinedString = new LinedString(join(lines, other.lines))

  def replace(start: Int, end: Int, chars: String): LinedString =
    new LinedString(replace(start, end, parseLines(chars)))

  lazy val wraps: ISeq[Int] = wrapsIn(lines, 0)

  private def charAt(index: Int, list: List[CharSequence]): Char = list match {
    case Nil          => throw new IndexOutOfBoundsException()
    case head :: tail => if (index < head.length) head.charAt(index) else charAt(index - head.length, tail)
  }

  private def subLines(start: Int, end: Int): List[CharSequence] = {
    if (start < 0 || end < 0) throw new IndexOutOfBoundsException()
    if (start > end) throw new IllegalArgumentException()
    if (start == end) {
      if (start <= length) List("") else throw new IndexOutOfBoundsException()
    } else {
      subLinesIn(start, end, lines)
    }
  }

  private def replace(start: Int, end: Int, lines: List[CharSequence]): List[CharSequence] = {
    val left  = subLines(0, start)
    val right = subLines(end, length)
    join(join(left, lines), right)
  }
}

private object LinedString {
  private def parseLines(s: String): List[CharSequence] = {
    val i = s.indexOf('\n')
    if (i == - 1) s :: Nil else s.substring(0, i + 1) :: parseLines(s.substring(i + 1))
  }

  private def subLinesIn(start: Int, end: Int, lines: List[CharSequence]): List[CharSequence] = lines match {
    case Nil => throw new IndexOutOfBoundsException()
    case head :: tail =>
      val l = head.length
      if (start < l) {
        if (end <= l) {
          val line = head.subSequence(start, end)
          if (endsWith(line, '\n')) line :: "" :: Nil else line :: Nil
        }
        else {
          head.subSequence(start, l) :: subLinesIn(0, end - l, tail)
        }
      } else {
        subLinesIn(start - l, end - l, tail)
      }
  }

  private def join(prefix: List[CharSequence], suffix: List[CharSequence]): List[CharSequence] =
    (prefix.reverse, suffix) match {
      case (pLast :: pInitReversed, sHead :: sTail) =>
        pInitReversed.reverse ::: List(pLast.toString + sHead.toString) ::: sTail
      case _ => prefix ::: suffix
    }

  // XXX TODO --- not tail recursive, may have huge stack costs
  private def wrapsIn(lines: List[CharSequence], offset: Int): List[Int] = lines match {
    case Nil      => throw new IllegalArgumentException()
    case _ :: Nil => Nil
    case h :: t   => (offset + h.length - 1) :: wrapsIn(t, offset + h.length )
  }

  private def endsWith(chars: CharSequence, c: Char) =
    chars.length > 0 && chars.charAt(chars.length - 1) == c
}