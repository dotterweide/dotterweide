/*
 *  IfParserTest.scala
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

class IfParserTest extends ParserTest(IfParser) {
  @Test
  def simple(): Unit = {
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
  def withElse(): Unit = {
    assertParsed("if (i > 0) { a = a + 1; } else { b = b + 2; }",
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
        ELSE
        block
          LBRACE
          assignment
            referenceToValue
              b
            EQ
            binaryExpression
              referenceToValue
                b
              PLUS
              literal
                2
            SEMI
          RBRACE
      """)
  }
}