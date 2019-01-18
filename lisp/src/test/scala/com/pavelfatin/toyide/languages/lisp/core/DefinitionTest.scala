/*
 *  DefinitionTest.scala
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

class DefinitionTest extends InterpreterTesting {
  @Test
  def definition(): Unit = {
    assertValue("(def x 1)", "()")

    assertError("(def x)")
    assertError("(def x 1 2)")

    assertValue("(def x 1) x", "1")

    assertOutput("(def x (trace 1))", "1")
    assertOutput("(def x (trace 1)) x x", "1")

    assertValue("(def x 1) (def y 2) x", "1")
    assertValue("(def x 1) (def y 2) y", "2")

    assertValue("(def x 1) (def x 2) x", "2")

    assertValue("def", "core.def")
    assertValue("(def f def) (f x 1) x", "1")

    assertError("x (def x 1)")
    assertError("(def x 1) y")

    assertValue("(def x 1) (def f (fn [] x)) (f)", "1")
    assertValue("(def f (fn [] (def x 1))) (f) x", "1")
    assertError("(def f (fn [] (def x 1))) x", "")
  }

  @Test
  def let(): Unit = {
    assertValue("(let [x 1])", "()")

    assertValue("(let [x 1] x)", "1")
    assertValue("(let [x 1 y 2] x)", "1")
    assertValue("(let [x 1 y 2] y)", "2")
    assertValue("(let [] 1 2)", "2")

    assertValue("(let [x 1 y [+ x 2]] y)", "3")
    assertError("(let [x [+ y 2] y 1] x)")

    assertError("(let)")
    assertError("(let 1)")
    assertError("(let [x])")
    assertError("(let [x 1 y])")

    assertOutput("(let [] (print 1) (print 2))", "12")

    assertOutput("(let [x [print 1]])", "1")
    assertOutput("(let [x [print 1] y [print 2]])", "12")

    assertOutput("(let [x [trace 1]] x x)", "1")

    assertValue("let", "core.let")
    assertValue("(def f let) (f (x 1) x)", "1")

    assertError("(let [x 1]) x")
    assertError("(let [x 1] y)")
  }
}