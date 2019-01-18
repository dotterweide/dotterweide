/*
 *  ReturnOutsideFunctionTest.scala
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

class ReturnOutsideFunctionTest extends InspectionTestBase(ReturnOutsideFunction) {
  @Test
  def inside(): Unit = {
    assertMatches(marksIn("def f(): void = { return; }")) {
      case Nil =>
    }
  }

  @Test
  def outside(): Unit = {
    assertMatches(marksIn("return;")) {
      case MarkData(_, ReturnOutsideFunction.Message) :: Nil =>
    }
  }
}