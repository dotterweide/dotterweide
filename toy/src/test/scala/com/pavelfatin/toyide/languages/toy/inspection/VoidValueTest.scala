/*
 *  VoidValueTest.scala
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

class VoidValueTest extends InspectionTestBase(VoidValue) {
  @Test
  def variable(): Unit = {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: void = 1;")) {
      case MarkData(Text("void"), VoidValue.Message) :: Nil =>
    }
  }

  @Test
  def parameter(): Unit = {
    assertMatches(marksIn("def f(p: integer): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(p: void): integer = {}")) {
      case MarkData(Text("void"), VoidValue.Message) :: Nil =>
    }
  }

  @Test
  def function(): Unit = {
    assertMatches(marksIn("def f(): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(): void = {}")) {
      case Nil =>
    }
  }
}