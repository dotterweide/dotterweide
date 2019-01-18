/*
 *  BindingTest.scala
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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class BindingTest extends InterpreterTesting {
  @Test
  def singular(): Unit = {
    assertValue("((fn []))", "()")

    assertValue("((fn [x] x) 1)", "1")
    assertValue("((fn [x y] x) 1 2)", "1")
    assertValue("((fn [x y] y) 1 2)", "2")

    assertError("((fn []) 1)")
    assertError("((fn [x]) 1 2)")

    assertError("((fn [x]))")
    assertError("((fn [x y]) 1)")
  }

  @Test
  def plural(): Unit = {
    assertValue("((fn [&]))", "()")

    assertValue("((fn [& x] x))", "()")
    assertValue("((fn [& x] x) 1)", "(1)")
    assertValue("((fn [& x] x) 1 2)", "(1 2)")

    assertValue("((fn [x & y] x) 1 2)", "1")
    assertValue("((fn [x & y] y) 1 2)", "(2)")

    assertValue("((fn [x y & z] x) 1 2 3 4)", "1")
    assertValue("((fn [x y & z] y) 1 2 3 4)", "2")
    assertValue("((fn [x y & z] z) 1 2 3 4)", "(3 4)")

    assertError("((fn [&]) 1)")
    assertError("((fn [x & y]))")
  }

  @Test
  def underscore(): Unit = {
    assertValue("((fn [_ y] y) 1 2)", "2")

    assertError("((fn [_] _) 1)")
  }
}