/*
 *  PredefinedIdentifierTest.scala
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

class PredefinedIdentifierTest extends InspectionTestBase(PredefinedIdentifier) {
  @Test
  def variable(): Unit = {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }
    assertMatches(marksIn("var print: integer = 1;")) {
      case Nil =>
    }
    assertMatches(marksIn("var println: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def parameter(): Unit = {
    assertMatches(marksIn("def f(p: integer): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(print: void): integer = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(println: void): integer = {}")) {
      case Nil =>
    }
  }

  @Test
  def function(): Unit = {
    assertMatches(marksIn("def f(): integer = {}")) {
      case Nil =>
    }

    val Message1 = PredefinedIdentifier.Message("print")
    assertMatches(marksIn("def print(): void = {}")) {
      case MarkData(Text("print"), Message1) :: Nil =>
    }

    val Message2 = PredefinedIdentifier.Message("println")
    assertMatches(marksIn("def println(): void = {}")) {
      case MarkData(Text("println"), Message2) :: Nil =>
    }
  }
}