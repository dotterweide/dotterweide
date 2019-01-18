/*
 *  OptimizationTest.scala
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

package com.pavelfatin.toyide.languages.toy.inspection

import org.junit.Test
import com.pavelfatin.toyide.inspection.MarkData
import com.pavelfatin.toyide.Helpers._

class OptimizationTest extends InspectionTestBase(Optimization) {
  @Test
  def literals(): Unit = {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: string = \"foo\";")) {
      case Nil =>
    }
  }

  @Test
  def expression(): Unit = {
    val Message = Optimization.Message("3")

    assertMatches(marksIn("var v: void = 1 + 2;")) {
        case MarkData(Text("1 + 2"), Message) :: Nil =>
    }
  }

  @Test
  def nestedExpression(): Unit = {
    val Message = Optimization.Message("6")

    assertMatches(marksIn("var v: void = 1 + 2 + 3;")) {
        case MarkData(Text("1 + 2 + 3"), Message) :: Nil =>
    }
  }
}