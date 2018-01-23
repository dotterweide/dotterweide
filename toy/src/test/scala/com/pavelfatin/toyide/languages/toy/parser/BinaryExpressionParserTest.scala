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

package com.pavelfatin.toyide.languages.toy.parser

import org.junit.Test

class BinaryExpressionParserTest extends ParserTest(ExpressionParser) {
  @Test
  def empty(): Unit = {
    assertParsed("",
      """
      error: leaf
      """)
  }

  @Test
  def number(): Unit = {
    assertParsed("1",
      """
      literal
        1
      """)
  }
  
  @Test
  def error(): Unit = {
    assertParsed("=",
      """
      error: EQ
      """)
    
    assertParsed("1 + =",
      """
      binaryExpression
        literal
          1
        PLUS
        error: EQ
      """)
  }
  
  @Test
  def continuation(): Unit = {
    assertParsed("1 =",
      """
      literal
        1
      """)
  }
  
  @Test
  def expected(): Unit = {
    assertParsed("1 +",
      """
      binaryExpression
        literal
          1
        PLUS
        error: leaf
      """)
  }

  @Test
  def braces(): Unit = {
    assertParsed("(1)",
      """
      group
        LPAREN
        literal
          1
        RPAREN
      """)
  }

  @Test
  def bracesEof(): Unit = {
    assertParsed("(1",
      """
      group
        LPAREN
        literal
          1
        error: leaf
      """)
  }

  @Test
  def bracesUnexpected(): Unit = {
    assertParsed("(1 true",
      """
      group
        LPAREN
        literal
          1
        error: true
      """)
  }

  @Test
  def multiplication(): Unit = {
    assertParsed("1 * 2",
      """
      binaryExpression
        literal
          1
        STAR
        literal
          2
      """)
  }

  @Test
  def prefix(): Unit = {
    assertParsed("-1",
      """
      prefixExpression
        MINUS
        literal
          1
      """)
  }

  @Test
  def prefixBoolean(): Unit = {
    assertParsed("!false",
      """
      prefixExpression
        BANG
        literal
          false
      """)
  }

  @Test
  def prefixMultiple(): Unit = {
    assertParsed("--1",
      """
      prefixExpression
        MINUS
        prefixExpression
          MINUS
          literal
            1
      """)
  }

  @Test
  def id(): Unit = {
    assertParsed("foo * bar",
      """
      binaryExpression
        referenceToValue
          foo
        STAR
        referenceToValue
          bar
      """)
  }

  @Test
  def callExp(): Unit = {
    assertParsed("foo(2 + 3) * bar",
      """
      binaryExpression
        callExpression
          referenceToFunction
            foo
          arguments
            LPAREN
            binaryExpression
              literal
                2
              PLUS
              literal
                3
            RPAREN
        STAR
        referenceToValue
          bar
      """)
  }

  @Test
  def division(): Unit = {
    assertParsed("1 / 2",
      """
      binaryExpression
        literal
          1
        SLASH
        literal
          2
      """)
  }

  @Test
  def addition(): Unit = {
    assertParsed("1 + 2",
      """
      binaryExpression
        literal
          1
        PLUS
        literal
          2
      """)
  }

  @Test
  def subtraction(): Unit = {
    assertParsed("1 - 2",
      """
      binaryExpression
        literal
          1
        MINUS
        literal
          2
      """)
  }

  @Test
  def series(): Unit = {
    assertParsed("1 + 2 - 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          PLUS
          literal
            2
        MINUS
        literal
          3
      """)
  }

  @Test
  def priority(): Unit = {
    assertParsed("1 + 2 * 3",
      """
      binaryExpression
        literal
          1
        PLUS
        binaryExpression
          literal
            2
          STAR
          literal
            3
      """)

    assertParsed("1 * 2 + 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          STAR
          literal
            2
        PLUS
        literal
          3
      """)
    
    assertParsed("(1 * 2) + 3",
      """
      binaryExpression
        group
          LPAREN
          binaryExpression
            literal
              1
            STAR
            literal
              2
          RPAREN
        PLUS
        literal
          3
      """)

    assertParsed("1 * (2 + 3)",
      """
      binaryExpression
        literal
          1
        STAR
        group
          LPAREN
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
  def relation(): Unit = {
    assertParsed("1 + 2 < 3",
      """
      binaryExpression
        binaryExpression
          literal
            1
          PLUS
          literal
            2
        LT
        literal
          3
      """)
  }

  @Test
  def equality(): Unit = {
    assertParsed("1 + 2 <= 3 == foo",
      """
      binaryExpression
        binaryExpression
          binaryExpression
            literal
              1
            PLUS
            literal
              2
          LT_EQ
          literal
            3
        EQ_EQ
        referenceToValue
          foo
      """)
  }

  @Test
  def boolean(): Unit = {
    assertParsed("(true == false)",
      """
      group
        LPAREN
        binaryExpression
          literal
            true
          EQ_EQ
          literal
            false
        RPAREN
      """)
  }

  @Test
  def logical(): Unit = {
    assertParsed("(true || false && true == false)",
      """
      group
        LPAREN
        binaryExpression
          literal
            true
          BAR_BAR
          binaryExpression
            literal
              false
            AMP_AMP
            binaryExpression
              literal
                true
              EQ_EQ
              literal
                false
        RPAREN
      """)
  }
}