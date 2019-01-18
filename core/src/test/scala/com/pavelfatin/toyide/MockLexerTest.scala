/*
 *  MockLexerTest.scala
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

import org.junit.Test
import org.junit.Assert._

class MockLexerTest {
  @Test
  def empty(): Unit = {
    assertTokens("", "")
  }

  @Test
  def letter(): Unit = {
    assertTokens("A", "token(A)")
  }

  @Test
  def letters(): Unit = {
    assertTokens("AB", "token(A), token(B)")
  }

  @Test
  def braces(): Unit = {
    assertTokens("()", "token((), token())")
    assertTokens("(Foo)", "token((), token(Foo), token())")
    assertTokens("{}", "token({), token(})")
  }

  @Test
  def single(): Unit = {
    assertTokens("Foo", "token(Foo)")
  }

  @Test
  def several(): Unit = {
    assertTokens("FooBar", "token(Foo), token(Bar)")
  }

  @Test
  def gap(): Unit = {
    assertTokens("Foo Bar", "token(Foo), token(Bar)")
  }

  @Test
  def span(): Unit = {
    val s = "FooBar"
    val tokens = MockLexer.analyze(s)
    assertEquals(Span(s, 0, 3), tokens.next().span)
    assertEquals(Span(s, 3, 6), tokens.next().span)
  }

  @Test
  def gapSpan(): Unit = {
    val s = "Foo Bar"
    val tokens = MockLexer.analyze(s)
    assertEquals(Span(s, 0, 3), tokens.next().span)
    assertEquals(Span(s, 4, 7), tokens.next().span)
  }

  def assertTokens(input: String, expectation: String): Unit = {
    assertEquals(expectation, MockLexer.analyze(input).map(_.toCompleteString).mkString(", "))
  }
}