/*
 *  DivisionByZeroTest.scala
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

package dotterweide.languages.toy.inspection

import org.junit.Test
import dotterweide.inspection.MarkData
import dotterweide.Helpers._

class DivisionByZeroTest extends InspectionTestBase(DivisionByZero) {
  @Test
  def division(): Unit = {
    assertMatches(marksIn("print(1 / 2);")) {
      case Nil =>
    }
  }

  @Test
  def divisionByZero(): Unit = {
    assertMatches(marksIn("print(1 / 0);")) {
        case MarkData(Text("1 / 0"), DivisionByZero.Message) :: Nil =>
    }
  }
}