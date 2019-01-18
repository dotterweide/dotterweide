/*
 *  UnresolvedReferenceTest.scala
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

class UnresolvedReferenceTest extends InspectionTestBase(UnresolvedReference) {
  @Test
  def variable(): Unit = {
    assertMatches(marksIn("var v: integer = 1; v = 2;")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("value", "v")

    assertMatches(marksIn("v = 2;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def parameter(): Unit = {
    assertMatches(marksIn("def f(p: integer): integer = { var v: integer = p; }")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("value", "p")

    assertMatches(marksIn("var v: integer = p;")) {
      case MarkData(Text("p"), Message) :: Nil =>
    }
  }

  @Test
  def function(): Unit = {
    assertMatches(marksIn("def f(): integer = {}; f();")) {
      case Nil =>
    }

    val Message = UnresolvedReference.Message("function", "f")

    assertMatches(marksIn("f();")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }

  @Test
  def builtInFunctions(): Unit = {
    assertMatches(marksIn("print();")) {
      case Nil =>
    }
    assertMatches(marksIn("println();")) {
      case Nil =>
    }
  }
}