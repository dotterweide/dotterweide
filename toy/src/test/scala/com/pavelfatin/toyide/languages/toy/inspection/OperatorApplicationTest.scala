/*
 *  OperatorApplicationTest.scala
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

class OperatorApplicationTest extends InspectionTestBase(OperatorApplication) {
  @Test
  def applicable(): Unit = {
    assertMatches(marksIn("println(1 + 2);")) {
      case Nil =>
    }
  }

  @Test
  def unknownOperandsType(): Unit = {
    assertMatches(marksIn("println(1 + v);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(v + 1);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(a + b);")) {
      case Nil =>
    }
  }

  @Test
  def inapplicable(): Unit = {
    val Message = OperatorApplication.Message("+", "integer", "boolean")

    assertMatches(marksIn("println(1 + true);")) {
      case MarkData(Text("1 + true"), Message) :: Nil =>
    }
  }
}