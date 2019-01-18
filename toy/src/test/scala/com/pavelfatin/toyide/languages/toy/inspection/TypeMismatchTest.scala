/*
 *  TypeMismatchTest.scala
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

class TypeMismatchTest extends InspectionTestBase(TypeMismatch) {
  @Test
  def variable(): Unit = {
    assertMatches(marksIn("var v: integer = 1;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: integer = foo;")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("var v: integer = true;")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def assignment(): Unit = {
    assertMatches(marksIn("var v: integer = 1; v = 2;")) {
      case Nil =>
    }

    assertMatches(marksIn("var v: integer = 1; v = foo;")) {
      case Nil =>
    }


    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("var v: integer = 1; v = true;")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def ifStatement(): Unit = {
    assertMatches(marksIn("if (true) {}")) {
      case Nil =>
    }

    assertMatches(marksIn("if (foo) {}")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("boolean", "integer")

    assertMatches(marksIn("if (1) {}")) {
      case MarkData(Text("1"), Message) :: Nil =>
    }
  }

  @Test
  def whileStatement(): Unit = {
    assertMatches(marksIn("while (true) {}")) {
      case Nil =>
    }

    assertMatches(marksIn("while (foo) {}")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("boolean", "integer")

    assertMatches(marksIn("while (1) {}")) {
      case MarkData(Text("1"), Message) :: Nil =>
    }
  }

  @Test
  def returnStatement(): Unit = {
    assertMatches(marksIn("def f(): integer = { return 1; }")) {
      case Nil =>
    }

    assertMatches(marksIn("def f(): integer = { return foo; }")) {
      case Nil =>
    }

    assertMatches(marksIn("return 1;")) {
      case Nil =>
    }

    val Message = TypeMismatch.Message("integer", "boolean")

    assertMatches(marksIn("def f(): integer = { return true; }")) {
      case MarkData(Text("true"), Message) :: Nil =>
    }
  }

  @Test
  def voidReturnStatement(): Unit = {
    assertMatches(marksIn("def f(): void = { return; }")) {
      case Nil =>
    }

    assertMatches(marksIn("def f(): void = { return 1; }")) {
      case MarkData(Text("return 1;"), TypeMismatch.ReturnFromVoidFunctionMessage) :: Nil =>
    }

    assertMatches(marksIn("def f(): integer = { return; }")) {
      case MarkData(Text("return;"), TypeMismatch.MissingReturnValueMessage) :: Nil =>
    }
  }
}