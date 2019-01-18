/*
 *  BlockParserTest.scala
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

class BlockParserTest extends ParserTest(BlockParser) {
  @Test
  def empty(): Unit = {
    assertParsed("{}",
      """
      block
        LBRACE
        RBRACE
      """)
  }

  @Test
  def incomplete(): Unit = {
    assertParsed("{",
      """
      block
        LBRACE
        error: leaf
      """)
  }

  @Test
  def variable(): Unit = {
    assertParsed("{ var a: integer = 1; }",
      """
      block
        LBRACE
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
        RBRACE
      """)
  }

  @Test
  def variables(): Unit = {
    assertParsed("{ var a: integer = 1; var b: string = \"foo\"; }",
      """
      block
        LBRACE
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
        variable
          VAR
          b
          typeSpec
            COLON
            STRING
          EQ
          literal
            "foo"
          SEMI
        RBRACE
      """)
  }

  @Test
  def assignment(): Unit = {
    assertParsed("{ a = 1; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            a
          EQ
          literal
            1
          SEMI
        RBRACE
      """)
  }

  @Test
  def assignments(): Unit = {
    assertParsed("{ a = 1; b = 2; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            a
          EQ
          literal
            1
          SEMI
        assignment
          referenceToValue
            b
          EQ
          literal
            2
          SEMI
        RBRACE
      """)
  }

  @Test
  def mixed(): Unit = {
    assertParsed("{ b = 1; var a: integer = 2; a = 3; }",
      """
      block
        LBRACE
        assignment
          referenceToValue
            b
          EQ
          literal
            1
          SEMI
        variable
          VAR
          a
          typeSpec
            COLON
            INTEGER
          EQ
          literal
            2
          SEMI
        assignment
          referenceToValue
            a
          EQ
          literal
            3
          SEMI
        RBRACE
      """)
  }
}