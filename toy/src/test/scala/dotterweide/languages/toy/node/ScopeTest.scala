/*
 *  ScopeTest.scala
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

package dotterweide.languages.toy.node

import dotterweide.Helpers
import dotterweide.languages.toy.ToyLexer
import dotterweide.languages.toy.parser.ProgramParser
import org.junit.Assert._
import org.junit.Test

class ScopeTest {
  @Test
  def empty(): Unit = {
    assertEquals(None, exitIn(""))
  }

  @Test
  def statements(): Unit = {
    assertEquals(None, exitIn("""
      var v: integer = 0;
      v = 1;
      def f(p: integer): void = {}
      f(2);
      while (true) {}
      if (true) {} else {}
      // comment
    """))
  }

  @Test
  def returnStatement(): Unit = {
    assertEquals(Some(0), exitIn("return;"))
  }

  @Test
  def severalExits(): Unit = {
    assertEquals(Some(1), exitIn("""
    var a: integer = 1;
    return;
    var b: integer = 2;
    return;
    """))
  }

  @Test
  def returnInsideHolders(): Unit = {
    assertEquals(None, exitIn("while (false) { return; }"))
    assertEquals(None, exitIn("def f(): void = { return; }"))
  }

  @Test
  def ifWithElse(): Unit = {
    assertEquals(None, exitIn("if (false) { return; }"))
    assertEquals(None, exitIn("if (false) {} else { return; }"))
    assertEquals(None, exitIn("if (false) { if (false) { return; } else {} } else { return; }"))
    assertEquals(None, exitIn("if (false) { return; } else { if (false) { return; } else {} }"))

    assertEquals(Some(0), exitIn("""
      if (false) {
        return;
      } else {
        return;
      }
      """))

    assertEquals(Some(0), exitIn("""
      if (false) {
        if (false) {
          return;
        } else {
          return;
        }
      } else {
        return;
      }
      """))

    assertEquals(Some(0), exitIn("""
      if (false) {
        return;
      } else {
        if (false) {
          return;
        } else {
          return;
        }
      }
      """))
  }

  protected def exitIn(code: String): Option[Int] = {
    val root = ProgramParser.parse(ToyLexer.analyze(code))
    val elements = root.elements
    Helpers.assertNoProblemsIn(elements)
    Helpers.assertNoUnresolvedIn(elements)
    val exit = root.asInstanceOf[Scope].exit
    exit.map(it => root.span.text.take(it.span.start).count(_ == '\n'))
  }
}