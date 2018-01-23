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

class LispLexerTest {
  @Test
  def empty(): Unit = {
    assertTokens("", "")
  }

  @Test
  def whitespace(): Unit = {
    assertTokens(" ", "WS( )")
    assertTokens("  ", "WS(  )")
    assertTokens("\t", "WS(\t)")
    assertTokens("\n", "WS(\n)")
    assertTokens("\r\n", "WS(\r\n)")
    assertTokens("\t  \r\n \t ", "WS(\t  \r\n \t )")
  }

  @Test
  def comment(): Unit = {
    assertTokens(";foo", "COMMENT(;foo)")
    assertTokens("; foo", "COMMENT(; foo)")
    assertTokens(";;", "COMMENT(;;)")
    assertTokens("; ; foo", "COMMENT(; ; foo)")
    assertTokens(";foo\n", "COMMENT(;foo), WS(\n)")
    assertTokens(";foo\n;bar", "COMMENT(;foo), WS(\n), COMMENT(;bar)")
  }

  @Test
  def integerLiteral(): Unit = {
    assertTokens("1", "INTEGER_LITERAL(1)")
    assertTokens("123", "INTEGER_LITERAL(123)")

    assertTokens("-123", "INTEGER_LITERAL(-123)")

    assertTokens("-foo", "CUSTOM_SYMBOL(-foo)")
  }

  @Test
  def booleanLiteral(): Unit = {
    assertTokens("true", "BOOLEAN_LITERAL(true)")
    assertTokens("false", "BOOLEAN_LITERAL(false)")
  }

  @Test
  def stringLiteral(): Unit = {
    assertTokens("\"abc\"", "STRING_LITERAL(\"abc\")")
    assertTokens("\"a\"", "STRING_LITERAL(\"a\")")
    assertTokens("\"a\" \"b\"", "STRING_LITERAL(\"a\"), WS( ), STRING_LITERAL(\"b\")")
    assertTokens("\"123\"", "STRING_LITERAL(\"123\")")
    assertTokens("\"def\"", "STRING_LITERAL(\"def\")")
  }

  @Test
  def unclosedStringLiteral(): Unit = {
    assertTokens("\"", "error: STRING_LITERAL(\")")
    assertTokens("\"a", "error: STRING_LITERAL(\"a)")
    assertTokens("\"abc", "error: STRING_LITERAL(\"abc)")

    assertTokens("\"\n", "error: STRING_LITERAL(\"), WS(\n)")
    assertTokens("\"abc\n", "error: STRING_LITERAL(\"abc), WS(\n)")

    assertTokens("\"abc\"\"", "STRING_LITERAL(\"abc\"), error: STRING_LITERAL(\")")
    assertTokens("\"a\"\"", "STRING_LITERAL(\"a\"), error: STRING_LITERAL(\")")
    assertTokens("\"\"\"", "STRING_LITERAL(\"\"), error: STRING_LITERAL(\")")
  }

  @Test
  def characterLiteral(): Unit = {
    assertTokens("\\a", "CHARACTER_LITERAL(\\a)")

    assertTokens("\\space", "CHARACTER_LITERAL(\\space)")
    assertTokens("\\tab", "CHARACTER_LITERAL(\\tab)")
    assertTokens("\\return", "CHARACTER_LITERAL(\\return)")
    assertTokens("\\newline", "CHARACTER_LITERAL(\\newline)")

    assertTokens("\\newlin", "CHARACTER_LITERAL(\\n), CUSTOM_SYMBOL(ewlin)")
    assertTokens("\\newlinX", "CHARACTER_LITERAL(\\n), CUSTOM_SYMBOL(ewlinX)")
  }

  @Test
  def unclosedCharacterLiteral(): Unit = {
    assertTokens("\\", "error: CHARACTER_LITERAL(\\)")
  }

  @Test
  def predefinedSymbol(): Unit = {
    assertTokens("let", "PREDEFINED_SYMBOL(let)")
  }

  @Test
  def customSymbol(): Unit = {
    assertTokens("name", "CUSTOM_SYMBOL(name)")
    assertTokens("name123", "CUSTOM_SYMBOL(name123)")
    assertTokens("name+-*/?><=&_%\'", "CUSTOM_SYMBOL(name+-*/?><=&_%\')")
    assertTokens("name1_a2'", "CUSTOM_SYMBOL(name1_a2')")

    assertTokens("let'", "CUSTOM_SYMBOL(let')")
    assertTokens("letlet", "CUSTOM_SYMBOL(letlet)")
  }

  @Test
  def char(): Unit = {
    assertTokens(",", "COMMA(,)")
    assertTokens("#", "HASH(#)")
    assertTokens("'", "QUOTE(')")
    assertTokens("`", "BACKQUOTE(`)")
    assertTokens("(", "LPAREN(()")
    assertTokens(")", "RPAREN())")
    assertTokens("[", "LBRACKET([)")
    assertTokens("]", "RBRACKET(])")
  }

  @Test
  def tilde(): Unit = {
    assertTokens("~", "TILDE(~)")
    assertTokens("~@", "TILDE_AT(~@)")

    assertTokens("~foo", "TILDE(~), CUSTOM_SYMBOL(foo)")
  }

  @Test
  def unknownChar(): Unit = {
    assertTokens("{", "error: UNKNOWN({)")
  }

  @Test
  def sequence(): Unit = {
    assertTokens("1 23  4", "INTEGER_LITERAL(1), WS( ), INTEGER_LITERAL(23), WS(  ), INTEGER_LITERAL(4)")
  }

  def assertTokens(input: String, expectation: String): Unit = {
    assertEquals(expectation, LispLexer.analyze(input).map(_.toCompleteString).mkString(", "))
  }
}