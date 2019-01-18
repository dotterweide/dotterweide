/*
 *  UserFunctionTest.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class UserFunctionTest extends InterpreterTesting {
  @Test
  def presentation(): Unit = {
    assertValue("(fn [])", "fn0")
    assertValue("(fn [x])", "fn1")
    assertValue("(fn [x y])", "fn2")

    assertValue("(fn [&])", "fn0")
    assertValue("(fn [& l])", "fn0*")
    assertValue("(fn [x & l])", "fn1*")
    assertValue("(fn [x y & l])", "fn2*")

    assertValue("(fn named [])", "named_fn0")
  }

  @Test
  def evaluation(): Unit = {
    assertError("(eval (fn []))")
  }

  @Test
  def application(): Unit = {
    assertValue("((fn []))", "()")
    assertValue("((fn [] 1))", "1")
    assertValue("((fn [] 1 2))", "2")

    assertOutput("((fn [] (trace 1) (trace 2)))", "12")
    assertOutput("((fn [x y]) (trace 1) (trace 2))", "12")
    assertOutput("((fn [x] (trace 2)) (trace 1))", "12")

    assertOutput("((fn [x] x x) (trace 1))", "1")
  }
}