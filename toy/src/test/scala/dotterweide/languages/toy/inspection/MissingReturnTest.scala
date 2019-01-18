/*
 *  MissingReturnTest.scala
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

class MissingReturnTest extends InspectionTestBase(MissingReturn) {
  @Test
  def program(): Unit = {
    assertMatches(marksIn("")) {
      case Nil =>
    }
  }

  @Test
  def voidFunction(): Unit = {
    assertMatches(marksIn("def f(): void = {}")) {
      case Nil =>
    }
  }

  @Test
  def nonVoidFunction(): Unit = {
    assertMatches(marksIn("def f(): integer = {}")) {
      case MarkData(Text("}"), MissingReturn.Message) :: Nil =>
    }
  }

  @Test
  def withReturn(): Unit = {
    assertMatches(marksIn("def f(): integer = { return 1; }")) {
      case Nil =>
    }
  }

  @Test
  def withComplexReturn(): Unit = {
    assertMatches(marksIn("def f(): integer = { if (true) { return 1; } else { return 2; } }")) {
      case Nil =>
    }
  }
}