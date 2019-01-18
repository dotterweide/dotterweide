/*
 *  UnusedDeclarationTest.scala
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

class UnusedDeclarationTest extends InspectionTestBase(UnusedDeclaration) {
  @Test
  def variable(): Unit = {
    assertMatches(marksIn("var v: integer = 1; println(v);")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("variable", "v")

    assertMatches(marksIn("var v: integer = 1;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def function(): Unit = {
    assertMatches(marksIn("def f(): integer = {}; println(f());")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("function", "f")

    assertMatches(marksIn("def f(): integer = {};")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }

  @Test
  def parameter(): Unit = {
    assertMatches(marksIn("def f(p: integer): void = { println(p); }; println(f());")) {
      case Nil =>
    }

    val Message = UnusedDeclaration.Message("parameter", "p")

    assertMatches(marksIn("def f(p: integer): void = {}; println(f());")) {
      case MarkData(Text("p"), Message) :: Nil =>
    }
  }

  @Test
  def order(): Unit = {
    val Message = UnusedDeclaration.Message("variable", "v")

    assertMatches(marksIn("println(v); var v: integer = 1;")) {
      case MarkData(Text("v"), Message) :: Nil =>
    }
  }

  @Test
  def nestedScope(): Unit = {
    assertMatches(marksIn("var v: integer = 1; if (true) { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; if (false) {} else { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; while (true) { println(v); }")) {
      case Nil =>
    }
    assertMatches(marksIn("var v: integer = 1; def f(): void = { println(v); }; println(f());")) {
      case Nil =>
    }
  }

  @Test
  def selfUsage(): Unit = {
    val Message = UnusedDeclaration.Message("function", "f")

    assertMatches(marksIn("def f(): integer = { println(f()); }")) {
      case MarkData(Text("f"), Message) :: Nil =>
    }
  }
}