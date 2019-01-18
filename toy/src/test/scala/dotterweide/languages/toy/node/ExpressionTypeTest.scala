/*
 *  ExpressionTypeTest.scala
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

package dotterweide.languages.toy.node

import dotterweide.languages.toy.ToyType._
import org.junit.Test

class ExpressionTypeTest extends ExpressionTypeTestBase {
  @Test
  def literals(): Unit = {
    assertTypeIs("\"foo\"", StringType)
    assertTypeIs("1", IntegerType)
    assertTypeIs("true", BooleanType)
  }

  @Test
  def reduce(): Unit = {
    assertTypeIs("\"foo\" + 1", StringType)
    assertTypeIs("1 + 2", IntegerType)
    assertNoType("1 + \"foo\"")
    assertTypeIs("\"foo\" + 1 + 2 + \"bar\"", StringType)
    assertNoType("1 + 2 + \"bar\"")
  }

  @Test
  def addition(): Unit = {
    assertTypeIs("1 + 2", IntegerType)
    assertTypeIs("1 - 2", IntegerType)
    assertTypeIs("\"foo\" + \"foo\"", StringType)
    assertNoType("\"foo\" - \"foo\"")
    assertTypeIs("\"foo\" + 1", StringType)
    assertNoType("\"foo\" - 1")
    assertNoType("1 + \"foo\"")
    assertNoType("1 - \"foo\"")
    assertNoType("true + true")
    assertNoType("1 + true")
    assertNoType("1 - true")
    assertTypeIs("\"foo\" + true", StringType)
    assertNoType("\"foo\" - true")
    assertNoType("true + \"foo\"")
    assertNoType("true - \"foo\"")
  }

  @Test
  def predefinedCall(): Unit = {
    assertTypeIs("print(1);", VoidType)
    assertTypeIs("println(1);", VoidType)
  }
}