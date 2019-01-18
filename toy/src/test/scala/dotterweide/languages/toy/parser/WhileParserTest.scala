/*
 *  WhileParserTest.scala
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

class WhileParserTest extends ParserTest(WhileParser) {
  @Test
  def normal(): Unit = {
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
}