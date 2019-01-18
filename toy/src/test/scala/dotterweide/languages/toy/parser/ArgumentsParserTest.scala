/*
 *  ArgumentsParserTest.scala
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

class ArgumentsParserTest extends ParserTest(ArgumentsParser) {
  @Test
  def empty(): Unit = {
    assertParsed("()",
      """
      arguments
        LPAREN
        RPAREN
      """)
  }

  @Test
  def incomplete(): Unit = {
    assertParsed("(",
      """
      arguments
        LPAREN
        error: leaf
      """)
  }

  @Test
  def single(): Unit = {
    assertParsed("(1)",
      """
      arguments
        LPAREN
        literal
          1
        RPAREN
      """)
  }

  @Test
  def pair(): Unit = {
    assertParsed("(1, 2 + 3)",
      """
      arguments
        LPAREN
        literal
          1
        COMMA
        binaryExpression
          literal
            2
          PLUS
          literal
            3
        RPAREN
      """)
  }

  @Test
  def triple(): Unit = {
    assertParsed("(foo, 5, true)",
      """
      arguments
        LPAREN
        referenceToValue
          foo
        COMMA
        literal
          5
        COMMA
        literal
          true
        RPAREN
      """)
  }
}