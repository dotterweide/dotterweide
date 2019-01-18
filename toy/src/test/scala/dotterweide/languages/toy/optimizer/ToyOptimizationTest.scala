/*
 *  ToyOptimizationTest.scala
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

package dotterweide.languages.toy.optimizer

import dotterweide.Helpers._
import dotterweide.document.DocumentImpl
import dotterweide.languages.toy.ToyLexer
import dotterweide.languages.toy.parser.ProgramParser
import dotterweide.optimizer.Optimizer
import org.junit.{Assert, Test}

class ToyOptimizationTest {
  @Test
  def literals(): Unit = {
    assertOptimized("print(1);", "print(1);")
    assertOptimized("print(\"foo\");", "print(\"foo\");")
    assertOptimized("print(true);", "print(true);")
  }

  @Test
  def prefixExpresion(): Unit = {
    assertOptimized("print(!true);", "print(false);")
    assertOptimized("print(!!true);", "print(true);")

    assertOptimized("print(!a);", "print(!a);")
  }

  @Test
  def groupExpresion(): Unit = {
    assertOptimized("print((1));", "print(1);")
  }

  @Test
  def binaryExpresion(): Unit = {
    assertOptimized("print(1 + 2);", "print(3);")

    assertOptimized("print(1 + a);", "print(1 + a);")
  }

  @Test
  def nestedExpresions(): Unit = {
    assertOptimized("print(1 + 2 + 3);", "print(6);")
  }
  
  @Test
  def multipleExpresions(): Unit = {
    assertOptimized("print(1 + 2); print(3 + 4);", "print(3); print(7);")
  }

  @Test
  def stingExpression(): Unit = {
    assertOptimized("print(\"foo\" + \" \" + 1);", "print(\"foo 1\");")
  }

  @Test
  def binaryExpresionLazy(): Unit = {
    val a = "var a: boolean = true; "

    assertOptimized("print(false && true);", "print(false);")
    assertOptimized(a + "print(false && a);", a + "print(false);")
    assertOptimized(a + "print(1 == 2 && a);", a + "print(false);")

    assertOptimized("print(true && true);", "print(true);")
    assertOptimized(a + "print(true && a);", a + "print(true && a);")

    assertOptimized(a + "print(1 != 1 && a);", a + "print(false);")
    assertOptimized(a + "print(1 / 0 == 1 && a);", a + "print(1 / 0 == 1 && a);")

    assertOptimized("print(true || false);", "print(true);")
    assertOptimized(a + "print(true || a);", a + "print(true);")
    assertOptimized(a + "print(1 == 1 || a);", a + "print(true);")

    assertOptimized("print(false || false);", "print(false);")
    assertOptimized(a + "print(false || a);", a + "print(false || a);")

    assertOptimized(a + "print(1 == 1 || a);", a + "print(true);")
    assertOptimized(a + "print(1 / 0 == 0 || a);", a + "print(1 / 0 == 0 || a);")
  }

  @Test
  def divisionByZero(): Unit = {
    assertOptimized("print(1 / 0);", "print(1 / 0);")
  }


  protected def assertOptimized(before: String, after: String): Unit = {
    val clean = before.filterNot(_ == '\r')
    val root = ProgramParser.parse(ToyLexer.analyze(clean))
    assertNoProblemsIn(root.elements)
    val document = new DocumentImpl(clean)
    Optimizer.optimize(root, document)
    Assert.assertEquals(after, document.text)
  }
}