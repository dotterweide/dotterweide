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