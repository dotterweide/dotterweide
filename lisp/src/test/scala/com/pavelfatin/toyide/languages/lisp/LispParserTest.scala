/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.lisp

import org.junit.Assert._
import org.junit.Test

class LispParserTest {
  @Test
  def empty(): Unit = {
    assertParsed("",
      """
        |program
      """)
  }

  @Test
  def comment(): Unit = {
    assertParsed("; foo",
      """
        |program
        |  comment
        |    ; foo
      """)
  }

  @Test
  def integerLiteral(): Unit = {
    assertParsed("1",
      """
        |program
        |  integer
        |    1
      """)
    assertParsed("'1",
      """
        |program
        |  integer
        |    QUOTE
        |    1
      """)
  }

  @Test
  def booleanLiteral(): Unit = {
    assertParsed("true",
      """
        |program
        |  boolean
        |    true
      """)
    assertParsed("'true",
      """
        |program
        |  boolean
        |    QUOTE
        |    true
      """)
  }

  @Test
  def characterLiteral(): Unit = {
    assertParsed("\\c",
      """
        |program
        |  character
        |    \c
      """)
    assertParsed("'\\c",
      """
        |program
        |  character
        |    QUOTE
        |    \c
      """)
  }

  @Test
  def stringLiteral(): Unit = {
    assertParsed("\"foo\"",
      """
        |program
        |  string
        |    "foo"
      """)
    assertParsed("'\"foo\"",
      """
        |program
        |  string
        |    QUOTE
        |    "foo"
      """)
  }

  @Test
  def predefinedSymbol(): Unit = {
    assertParsed("let",
      """
        |program
        |  symbol
        |    let
      """)
    assertParsed("'let",
      """
        |program
        |  symbol
        |    QUOTE
        |    let
      """)
  }

  @Test
  def customSymbol(): Unit = {
    assertParsed("name",
      """
        |program
        |  symbol
        |    name
      """)
    assertParsed("'name",
      """
        |program
        |  symbol
        |    QUOTE
        |    name
      """)
  }

  @Test
  def prefixes(): Unit = {
    assertParsed("'1",
      """
        |program
        |  integer
        |    QUOTE
        |    1
      """)
    assertParsed("~1",
      """
        |program
        |  integer
        |    TILDE
        |    1
      """)
    assertParsed("~@1",
      """
        |program
        |  integer
        |    TILDE_AT
        |    1
      """)
    assertParsed("`1",
      """
        |program
        |  integer
        |    BACKQUOTE
        |    1
      """)
    assertParsed("#1",
      """
        |program
        |  integer
        |    HASH
        |    1
      """)
  }

  @Test
  def program(): Unit = {
    assertParsed("1 2 3",
      """
        |program
        |  integer
        |    1
        |  integer
        |    2
        |  integer
        |    3
      """)
  }

  @Test
  def list(): Unit = {
    assertParsed("()",
      """
        |program
        |  list
        |    LPAREN
        |    RPAREN
      """)

    assertParsed("(1)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    RPAREN
      """)

    assertParsed("(1 2 3)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    integer
        |      2
        |    integer
        |      3
        |    RPAREN
      """)
  }

  @Test
  def listPrefix(): Unit = {
    assertParsed("'()",
      """
        |program
        |  list
        |    QUOTE
        |    LPAREN
        |    RPAREN
      """)
  }

  @Test
  def commasInList(): Unit = {
    assertParsed("(1, 2, 3)",
      """
        |program
        |  list
        |    LPAREN
        |    integer
        |      1
        |    COMMA
        |    integer
        |      2
        |    COMMA
        |    integer
        |      3
        |    RPAREN
      """)
  }

  @Test
  def listAsVector(): Unit = {
    assertParsed("[1]",
      """
        |program
        |  list
        |    LBRACKET
        |    integer
        |      1
        |    RBRACKET
      """)
  }

  @Test
  def unclosedList(): Unit = {
    assertParsed("(",
      """
        |program
        |  list
        |    LPAREN
        |    error: leaf
      """)

    assertParsed("[",
      """
        |program
        |  list
        |    LBRACKET
        |    error: leaf
      """)
  }

  @Test
  def listBoundMismatch(): Unit = {
    assertParsed("(]",
      """
        |program
        |  list
        |    LPAREN
        |    error: RBRACKET
      """)

    assertParsed("[)",
      """
        |program
        |  list
        |    LBRACKET
        |    error: RPAREN
      """)
  }

  private def assertParsed(code: String, expectation: String): Unit = {
    assertEquals(format(expectation), parsed(code))
  }

  private def parsed(code: String): String =
    LispParser.parse(LispLexer.analyze(code)).content.trim

  private def format(expectation: String) =
    expectation.replace("\r", "").trim.stripMargin
}