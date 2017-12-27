/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.toy.parser

import org.junit.Test

class BlockStatementParserTest extends ParserTest(BlockStatementParser) {
  @Test
  def variable(): Unit = {
    assertParsed("var a: integer = 1;",
      """
      variable
        VAR
        a
        typeSpec
          COLON
          INTEGER
        EQ
        literal
          1
        SEMI
      """)
  }

    @Test
  def assignment(): Unit = {
      assertParsed("a = 1;",
          """
          assignment
            referenceToValue
              a
            EQ
            literal
              1
            SEMI
          """)
    }

  @Test
  def returnStatement(): Unit = {
    assertParsed("return;",
      """
      return
        RETURN
        SEMI
      """)
  }

   @Test
  def whileStatement(): Unit = {
     assertParsed("while (i > 0) { a = a + 1; }",
        """
        while
          WHILE
          LPAREN
          binaryExpression
            referenceToValue
              i
            GT
            literal
              0
          RPAREN
          block
            LBRACE
            assignment
              referenceToValue
                a
              EQ
              binaryExpression
                referenceToValue
                  a
                PLUS
                literal
                  1
              SEMI
            RBRACE
        """)
   }

  @Test
  def ifStatement(): Unit = {
    assertParsed("if (i > 0) { a = a + 1; }",
      """
      if
        IF
        LPAREN
        binaryExpression
          referenceToValue
            i
          GT
          literal
            0
        RPAREN
        block
          LBRACE
          assignment
            referenceToValue
              a
            EQ
            binaryExpression
              referenceToValue
                a
              PLUS
              literal
                1
            SEMI
          RBRACE
      """)
  }

  @Test
  def call(): Unit = {
    assertParsed("foo(1, 2);",
      """
      call
        callExpression
          referenceToFunction
            foo
          arguments
            LPAREN
            literal
              1
            COMMA
            literal
              2
            RPAREN
        SEMI
      """)
  }

  @Test
  def comment(): Unit = {
    assertParsed("// foo bar",
      """
      comment
        // foo bar
      """)
  }

  @Test
  def empty(): Unit = {
    assertParsed(";",
      """
      empty
        SEMI
      """)
  }

  @Test
  def wrong(): Unit = {
    assertParsed("foo",
      """
      error: foo
      """)
  }
}