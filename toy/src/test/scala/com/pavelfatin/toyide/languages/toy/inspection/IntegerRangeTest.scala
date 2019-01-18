/*
 *  IntegerRangeTest.scala
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

class IntegerRangeTest extends InspectionTestBase(IntegerRange) {
  @Test
  def normal(): Unit = {
    assertMatches(marksIn("var v: integer = 123;")) {
      case Nil =>
    }
  }

  @Test
  def tooLarge(): Unit = {
    assertMatches(marksIn("var v: integer = 2147483648;")) {
      case MarkData(Text("2147483648"), IntegerRange.Message) :: Nil =>
    }
  }
}