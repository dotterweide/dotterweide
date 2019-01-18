/*
 *  UnreachableStatementTest.scala
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

class UnreachableStatementTest extends InspectionTestBase(UnreachableStatement) {
  @Test
  def program(): Unit = {
    assertMatches(marksIn("return; println();")) {
      case Nil =>
    }
  }

  @Test
  def returnIsLastStatement(): Unit = {
    assertMatches(marksIn("def f(): void = { println(); return; }")) {
      case Nil =>
    }
  }

  @Test
  def returnIsNestedStatement(): Unit = {
    assertMatches(marksIn("def f(): void = { if (false) { return; } println(); }")) {
      case Nil =>
    }
  }

  @Test
  def unreachableStatement(): Unit = {
    assertMatches(marksIn("def f(): void = { return; println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def unreachableStatements(): Unit = {
    assertMatches(marksIn("def f(): void = { return; println(); print(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def unreachableStatementWithComplexReturn(): Unit = {
    assertMatches(marksIn("def f(): void = { if (true) { return; } else { return; } println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }

  @Test
  def comment(): Unit = {
    assertMatches(marksIn("def f(): void = { return; // comment\n }")) {
      case Nil =>
    }
    assertMatches(marksIn("def f(): void = { return; // comment 1\n //comment 2\n println(); }")) {
      case MarkData(Text("println();"), UnreachableStatement.Message) :: Nil =>
    }
  }
}