/*
 *  ApplicabilityTest.scala
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

class ApplicabilityTest extends InspectionTestBase(Applicability) {
  @Test
  def fine(): Unit = {
    assertMatches(marksIn("def f(): void = {}; f();")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(a: integer, b: boolean): void = {}; f(1, true);")) {
      case Nil =>
    }
    assertMatches(marksIn("println(1, true);")) {
      case Nil =>
    }
  }

  @Test
  def excessive(): Unit = {
    val Message1 = Applicability.Excessive("f(): void")

    assertMatches(marksIn("def f(): void = {}; f(1);")) {
      case MarkData(Text("1"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Excessive("f(a: integer, b: integer): void")

    assertMatches(marksIn("def f(a: integer, b: integer): void = {}; f(1, 2, 3, 4);")) {
      case MarkData(Text("3"), Message2) :: MarkData(Text("4"), Message2) :: Nil =>
    }
  }

  @Test
  def missed(): Unit = {
    val Message1 = Applicability.Missed("f(a: integer): void", "a")

    assertMatches(marksIn("def f(a: integer): void = {}; f();")) {
      case MarkData(Text(")"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Missed("f(a: integer, b: integer, c: integer, d: integer): void", "c, d")

    assertMatches(marksIn("def f(a: integer, b: integer, c: integer, d: integer): void = {}; f(1, 2);")) {
      case MarkData(Text(")"), Message2) :: Nil =>
    }
  }

  @Test
  def mismatch(): Unit = {
    val Message1 = Applicability.Mismatch("integer", "boolean")

    assertMatches(marksIn("def f(a: integer): void = {}; f(true);")) {
      case MarkData(Text("true"), Message1) :: Nil =>
    }

    val Message2 = Applicability.Mismatch("boolean", "integer")

    assertMatches(marksIn("def f(a: integer, b: boolean): void = {}; f(true, 1);")) {
      case MarkData(Text("true"), Message1) :: MarkData(Text("1"), Message2) :: Nil =>
    }
  }

  @Test
  def voidToPredefined(): Unit = {
    assertMatches(marksIn("print(print());")) {
      case MarkData(Text("print()"), Applicability.Void) :: Nil =>
    }

    assertMatches(marksIn("print(print(1), print(2));")) {
      case MarkData(Text("print(1)"), Applicability.Void) :: MarkData(Text("print(2)"), Applicability.Void) :: Nil =>
    }
  }
}