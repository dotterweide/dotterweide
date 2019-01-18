/*
 *  BlockStatementParserTest.scala
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

package dotterweide.languages.toy.parser

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