/*
 *  PrefixExpressionTypeTest.scala
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

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.languages.toy.ToyType._
import org.junit.Test

class PrefixExpressionTypeTest extends ExpressionTypeTestBase {
  @Test
  def positive(): Unit = {
    assertTypeIs("+1", IntegerType)
  }

  @Test
  def negative(): Unit = {
    assertTypeIs("-1", IntegerType)
  }

  @Test
  def recursive(): Unit = {
    assertTypeIs("+++1", IntegerType)
  }

  @Test
  def wrong(): Unit = {
    assertNoType("-true")
    assertNoType("-\"foo\"")
  }

  @Test
  def not(): Unit = {
    assertTypeIs("!false", BooleanType)
  }

  @Test
  def notWrong(): Unit = {
    assertNoType("!1")
  }
}