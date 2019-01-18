/*
 *  ReadingTest.scala
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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Assert._
import org.junit._

class ReadingTest extends InterpreterTesting {
  @Test
  def expressions(): Unit = {
    assertRead("1", "1")
    assertRead("true", "true")
    assertRead("\\c", "\\c")
    assertRead("()", "()")
    assertRead("symbol", "symbol")
  }

  @Test
  def specialCharacters(): Unit = {
    assertRead("\\space", "\\space")
    assertRead("\\tab", "\\tab")
    assertRead("\\return", "\\return")
    assertRead("\\newline", "\\newline")
  }

  @Test
  def string(): Unit = {
    assertRead("\"string\"", "(list \\s \\t \\r \\i \\n \\g)")
  }

  @Test
  def stringEscapes(): Unit = {
    assertRead("\"\\t\"", "(list \\tab)")
    assertRead("\"\\r\"", "(list \\return)")
    assertRead("\"\\n\"", "(list \\newline)")
    assertRead("\"\\\"", "(list \\\\)")
  }

  @Test
  def quote(): Unit = {
    assertRead("'symbol", "(quote symbol)")
  }

  @Test
  def unquote(): Unit = {
    assertRead("~symbol", "(unquote symbol)")
  }

  @Test
  def unquoteSplicing(): Unit = {
    assertRead("~@symbol", "(unquote-splicing symbol)")
  }

  @Test
  def quasiquote(): Unit = {
    assertRead("`symbol", "(quasiquote symbol)")
  }

  @Test
  def withinList(): Unit = {
    assertRead("('x ~y `z)", "((quote x) (unquote y) (quasiquote z))")
  }

  @Test
  def functionLiteral(): Unit = {
    assertRead("#()", "(fn () ())")

    assertRead("#(%)", "(fn (_p1) (_p1))")
    assertRead("#(%1)", "(fn (_p1) (_p1))")
    assertRead("#(%2)", "(fn (_p1 _p2) (_p2))")

    assertRead("#(% %)", "(fn (_p1) (_p1 _p1))")
    assertRead("#(%1 %1)", "(fn (_p1) (_p1 _p1))")
    assertRead("#(%2 %2)", "(fn (_p1 _p2) (_p2 _p2))")

    assertRead("#(%1 %2)", "(fn (_p1 _p2) (_p1 _p2))")
    assertRead("#(%2 %1)", "(fn (_p1 _p2) (_p2 _p1))")

    assertRead("#(%&)", "(fn (& _ps) (_ps))")
    assertRead("#(%& %&)", "(fn (& _ps) (_ps _ps))")
    assertRead("#(% %&)", "(fn (_p1 & _ps) (_p1 _ps))")
    assertRead("#(%& %)", "(fn (_p1 & _ps) (_ps _p1))")

    assertRead("#(do %)", "(fn (_p1) (do _p1))")

    assertRead("#(do (do %))", "(fn (_p1) (do (do _p1)))")
  }

  private def assertRead(code: String, expected: String): Unit = {
    val root = InterpreterTesting.parse(code)
    val readable = root.elements.findBy[ReadableNode].getOrElse(
      throw new RuntimeException("No readable element found"))
    assertEquals(expected, readable.read(InterpreterTesting.Source).presentation)
  }
}