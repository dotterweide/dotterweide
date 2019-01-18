/*
 *  ParametersParserTest.scala
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

class ParametersParserTest extends ParserTest(ParametersParser) {
  @Test
  def empty(): Unit = {
    assertParsed("()",
      """
      parameters
        LPAREN
        RPAREN
      """)
  }

  @Test
  def incomplete(): Unit = {
    assertParsed("(",
      """
      parameters
        LPAREN
        error: leaf
      """)
  }

  @Test
  def single(): Unit = {
    assertParsed("(foo: integer)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        RPAREN
      """)
  }

  @Test
  def pair(): Unit = {
    assertParsed("(foo: integer, bar: string)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        COMMA
        parameter
          bar
          typeSpec
            COLON
            STRING
        RPAREN
      """)
  }

  @Test
  def triple(): Unit = {
    assertParsed("(foo: integer, bar: string, moo: boolean)",
      """
      parameters
        LPAREN
        parameter
          foo
          typeSpec
            COLON
            INTEGER
        COMMA
        parameter
          bar
          typeSpec
            COLON
            STRING
        COMMA
        parameter
          moo
          typeSpec
            COLON
            BOOLEAN
        RPAREN
      """)
  }
}