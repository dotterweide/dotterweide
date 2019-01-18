/*
 *  ComparisonTest.scala
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

class ComparisonTest extends InterpreterTesting {
  @Test
  def gt(): Unit = {
    assertValue("(> 1 2)", "false")
    assertValue("(> 2 1)", "true")
    assertValue("(> 1 1)", "false")

    assertOutput("(> (trace 1) (trace 2))", "12")

    assertValue(">", "core.>")
    assertValue("(def f >) (f 1 2)", "false")

    assertError("(> 1 true)")
    assertError("(> true 2)")
    assertError("(> true true)")
    assertError("(>)")
    assertError("(> 1)")
    assertError("(> 1 2 3)")
  }

  @Test
  def gtEq(): Unit = {
    assertValue("(>= 1 2)", "false")
    assertValue("(>= 2 1)", "true")
    assertValue("(>= 1 1)", "true")

    assertOutput("(>= (trace 1) (trace 2))", "12")

    assertValue(">=", "core.>=")
    assertValue("(def f >=) (f 1 2)", "false")

    assertError("(>= 1 true)")
    assertError("(>= true 2)")
    assertError("(>= true true)")
    assertError("(>=)")
    assertError("(>= 1)")
    assertError("(>= 1 2 3)")
  }

  @Test
  def lt(): Unit = {
    assertValue("(< 1 2)", "true")
    assertValue("(< 2 1)", "false")
    assertValue("(< 1 1)", "false")

    assertOutput("(< (trace 1) (trace 2))", "12")

    assertValue("<", "core.<")
    assertValue("(def f <) (f 1 2)", "true")

    assertError("(< 1 true)")
    assertError("(< true 2)")
    assertError("(< true true)")
    assertError("(<)")
    assertError("(< 1)")
    assertError("(< 1 2 3)")
  }

  @Test
  def ltEq(): Unit = {
    assertValue("(<= 1 2)", "true")
    assertValue("(<= 2 1)", "false")
    assertValue("(<= 1 1)", "true")

    assertOutput("(<= (trace 1) (trace 2))", "12")

    assertValue("<=", "core.<=")
    assertValue("(def f <=) (f 1 2)", "true")

    assertError("(<= 1 true)")
    assertError("(<= true 2)")
    assertError("(<= true true)")
    assertError("(<=)")
    assertError("(<= 1)")
    assertError("(<= 1 2 3)")
  }

  @Test
  def eq(): Unit = {
    assertValue("(= 1 2)", "false")
    assertValue("(= 2 1)", "false")
    assertValue("(= 1 1)", "true")

    assertValue("(= false true)", "false")
    assertValue("(= true false)", "false")
    assertValue("(= true true)", "true")
    assertValue("(= false false)", "true")

    assertValue("(= \\a \\b)", "false")
    assertValue("(= \\a \\a)", "true")

    assertValue("(= '() '())", "true")
    assertValue("(= '() '(1))", "false")
    assertValue("(= '(1) '(1))", "true")
    assertValue("(= '(1) '(2))", "false")
    assertValue("(= '(1) '(1 2))", "false")
    assertValue("(= '(1 2) '(1))", "false")
    assertValue("(= '(1 2) '(1 2))", "true")

    assertValue("(= '(1 (2 3)) '(1 (2 3)))", "true")
    assertValue("(= '(1 (2 3)) '(1 (2 4)))", "false")

    assertOutput("(= (trace 1) (trace 2))", "12")

    assertValue("=", "core.=")
    assertValue("(def f =) (f 1 2)", "false")

    assertError("(= 1 true)")
    assertError("(= true 2)")
    assertError("(= '(1) '(true))")
    assertError("(=)")
    assertError("(= 1)")
    assertError("(= 1 2 3)")
  }
}