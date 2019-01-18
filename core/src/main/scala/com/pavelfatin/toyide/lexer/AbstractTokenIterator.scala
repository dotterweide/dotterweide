/*
 *  AbstractTokenIterator.scala
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

package com.pavelfatin.toyide.lexer

import com.pavelfatin.toyide.Span

abstract class AbstractTokenIterator(input: CharSequence) extends Iterator[Token] {
  private var index   = 0
  private var marker  = -1

  def advance(): Unit =
    advance(1)

  def advance(count: Int): Unit =
    index += count

  def mark(): Unit =
    marker = index

  def marked: Span = Span(input, marker, index)

  def captureChar: Span = captureChars(1)

  def captureChars(count: Int): Span = {
    mark()
    Range(0, count).foreach(_ => advance())
    marked
  }

  def skip(predicate: Char => Boolean): Unit = {
    while (hasNext && predicate(char)) advance()
  }

  def capture(predicate: Char => Boolean): Span = {
    mark()
    skip(predicate)
    marked
  }

  def char: Char = input.charAt(index)

  def ahead(offset: Int): Option[Char] = {
    val i = index + offset
    if (i < input.length) Some(input.charAt(i)) else None
  }

  def isAhead(char: Char): Boolean = isAhead(_ == char)

  def isAhead(predicate: Char => Boolean): Boolean = ahead(1).exists(predicate)

  def isAhead(string: String): Boolean = {
    val end = index + string.length
    end <= input.length && input.subSequence(index, end).toString == string
  }

  def hasNext: Boolean = index < input.length
}
