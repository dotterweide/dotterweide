/*
 *  ArithmeticTest.scala
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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class ArithmeticTest extends InterpreterTesting {
  @Test
  def add(): Unit = {
    assertValue("(+)", "0")

    assertValue("(+ 1)", "1")
    assertValue("(+ 1 2)", "3")
    assertValue("(+ 1 2 3)", "6")

    assertOutput("(+ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("+", "core.+")
    assertValue("(def f +) (f 1 2)", "3")

    assertError("(+ true)")
  }

  @Test
  def sub(): Unit = {
    assertValue("(- 1)", "-1")

    assertValue("(- 3 1)", "2")
    assertValue("(- 3 2 5)", "-4")

    assertOutput("(- (trace 1) (trace 2) (trace 3))", "123")

    assertValue("-", "core.-")
    assertValue("(def f -) (f 1 2)", "-1")

    assertError("(-)")
    assertError("(- true)")
  }

  @Test
  def mul(): Unit = {
    assertValue("(*)", "1")

    assertValue("(* 2)", "2")
    assertValue("(* 2 3)", "6")
    assertValue("(* 2 3 4)", "24")

    assertOutput("(* (trace 1) (trace 2) (trace 3))", "123")

    assertValue("*", "core.*")
    assertValue("(def f *) (f 2 3)", "6")

    assertError("(* true)")
  }

  @Test
  def div(): Unit = {
    assertValue("(/ 6 3)", "2")
    assertValue("(/ 6 3 2 )", "1")

    assertValue("(/ 0 1)", "0")

    assertOutput("(/ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("/", "core./")
    assertValue("(def f /) (f 6 3)", "2")

    assertError("(/)")
    assertError("(/ 1 0)", "zero")
    assertError("(/ 1 0 2)", "zero")
    assertError("(/ true)")
  }

  @Test
  def mod(): Unit = {
    assertValue("(mod 5 3)", "2")
    assertValue("(mod 6 3)", "0")

    assertValue("(mod 0 1)", "0")

    assertOutput("(mod (trace 1) (trace 2))", "12")

    assertValue("mod", "core.mod")
    assertValue("(def f mod) (f 5 3)", "2")

    assertError("(mod)")
    assertError("(mod 1 2 3)")
    assertError("(mod 1 0)", "zero")
    assertError("(mod true true)")
  }
}