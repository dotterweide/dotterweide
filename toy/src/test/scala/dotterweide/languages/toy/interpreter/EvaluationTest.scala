/*
 *  EvaluationTest.scala
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

package dotterweide.languages.toy.interpreter

import dotterweide.interpreter.{EvaluationException, Place}
import dotterweide.languages.toy.EvaluationTestBase
import org.junit.{Assert, Test}

class EvaluationTest extends EvaluationTestBase with InterpreterTesting {
  @Test
  def stackOverflow(): Unit = {
    run("def f(): void = { f(); }")

    try {
      run("def f(): void = { f(); }; f();")
    } catch {
      case EvaluationException(message, _) if message == "Stack overflow" => return
    }

    Assert.fail("Expecting stack overflow exception")
  }

  @Test
  def stackOverflowWithParameterAllocations(): Unit = {
    run("def f(p: integer): void = { f(1); }")

    try {
      run("def f(p: integer): void = { f(1); }; f(2);")
    } catch {
      case EvaluationException(message, _) if message == "Stack overflow" => return
    }

    Assert.fail("Expecting stack overflow exception")
  }

  @Test(expected = classOf[EvaluationException])
  def valueAllocationsInSingleScope(): Unit = {
    run("var a: integer = 1; var a: boolean = true;")
  }

  @Test(expected = classOf[EvaluationException])
  def valueAllocationsInSingleScopeInFrame(): Unit = {
    run("def f(): void = { var a: integer = 1; var a: boolean = true; }; f();")
  }

  @Test
  def valueAllocationsInDifferentScopes(): Unit = {
    run("if (true) { var a: integer = 1; }; if (true) { var a: boolean = true; }")
    run("def f(): void = { if (true) { var a: integer = 1; }; if (true) { var a: boolean = true; } }; f();")
  }

  @Test
  def simpleTrace(): Unit = {
    try {
      run("""
      print(1);
      print(2);
      print(3 / 0);
      print(4);
      """)
    } catch {
      case EvaluationException(_, trace) =>
        Assert.assertEquals(List(Place(None, 3)), trace.toList)
        return
    }

    Assert.fail("Expecting division by zero exception")
  }

  @Test
  def complexTrace(): Unit = {
    try {
      run("""
      def a(): void = {
        print(1 / 0);
      }
      def b(): void = {
        a();
      }
      def c(): void = {
        b();
      }
      c();
      """)
    } catch {
      case EvaluationException(_, trace) =>
        val expected = List(
          Place(Some("a"), 2),
          Place(Some("b"), 5),
          Place(Some("c"), 8),
          Place(None, 10))

        Assert.assertEquals(expected, trace.toList)

        return
    }

    Assert.fail("Expecting division by zero exception")
  }
}