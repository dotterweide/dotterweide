/*
 *  FunctionTest.scala
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

class FunctionTest extends InterpreterTesting {
  @Test
  def fn(): Unit = {
    assertValue("(fn [])", "fn0")
    assertValue("(fn [x])", "fn1")
    assertValue("(fn [x y])", "fn2")
    assertValue("(fn [] 1)", "fn0")
    assertValue("(fn [] 1 2)", "fn0")

    assertValue("(fn name [x y] 1 2)", "name_fn2")

    assertError("(fn)")
    assertError("(fn 1)")
    assertError("(fn 1 [])")
    assertError("(fn name)")
    assertError("(fn name 1)")

    assertValue("fn", "core.fn")
    assertValue("(def f fn) (f [x])", "fn1")
  }

  @Test
  def apply(): Unit = {
    assertValue("(apply + '())", "0")
    assertValue("(apply + '(1))", "1")
    assertValue("(apply + '(1 2))", "3")

    assertError("(apply 1 '())")
    assertError("(apply + 1)")
    assertError("(apply + '() '())")

    assertValue("apply", "core.apply")
    assertValue("(def f apply) (f + '(1 2))", "3")
  }
}