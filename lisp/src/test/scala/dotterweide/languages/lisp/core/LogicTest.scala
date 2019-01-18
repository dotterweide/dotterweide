/*
 *  LogicTest.scala
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

package dotterweide.languages.lisp.core

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class LogicTest extends InterpreterTesting {
  @Test
  def and(): Unit = {
    assertValue("(and)", "true")

    assertValue("(and true)", "true")
    assertValue("(and false)", "false")

    assertValue("(and true true)", "true")
    assertValue("(and true false)", "false")
    assertValue("(and false true)", "false")
    assertValue("(and false false)", "false")

    assertValue("(and true true true)", "true")
    assertValue("(and false true true)", "false")
    assertValue("(and true false true)", "false")
    assertValue("(and true true false)", "false")

    assertOutput("(and (trace true) (trace false) (trace true))", "truefalse")

    assertValue("and", "core.and")
    assertValue("(def f and) (f true false)", "false")

    assertError("(and 1)")
  }

  @Test
  def or(): Unit = {
    assertValue("(or)", "false")

    assertValue("(or true)", "true")
    assertValue("(or false)", "false")

    assertValue("(or true true)", "true")
    assertValue("(or true false)", "true")
    assertValue("(or false true)", "true")
    assertValue("(or false false)", "false")

    assertValue("(or false false false)", "false")
    assertValue("(or true false false)", "true")
    assertValue("(or false true false)", "true")
    assertValue("(or false false true)", "true")

    assertOutput("(or (trace false) (trace true) (trace false))", "falsetrue")

    assertValue("or", "core.or")
    assertValue("(def f or) (f true false)", "true")

    assertError("(or 1)")
  }

  @Test
  def not(): Unit = {
    assertValue("(not false)", "true")
    assertValue("(not true)", "false")

    assertValue("not", "core.not")
    assertValue("(def f not) (f false)", "true")

    assertError("(not)")
    assertError("(not 1)")
    assertError("(not true true)")
  }
}