/*
 *  StructuralBindingTest.scala
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

package dotterweide.languages.lisp.parameters

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class StructuralBindingTest extends InterpreterTesting {
  @Test
  def singular(): Unit = {
    assertValue("((fn [[]]) (list))", "()")

    assertValue("((fn [[x]] x) (list 1))", "1")
    assertValue("((fn [[x y]] x) (list 1 2))", "1")
    assertValue("((fn [[x y]] y) (list 1 2))", "2")

    assertError("((fn [[]]))")
    assertError("((fn [[]]) 1)")

    assertError("((fn [[]]) (list 1))")
    assertError("((fn [[x]]) (list 1 2))")

    assertError("((fn [[x]]) (list))")
    assertError("((fn [[x y]]) (list 1))")
  }

  @Test
  def plural(): Unit = {
    assertValue("((fn [[&]]) (list))", "()")

    assertValue("((fn [[& x]] x) (list))", "()")
    assertValue("((fn [[& x]] x) (list 1))", "(1)")
    assertValue("((fn [[& x]] x) (list 1 2))", "(1 2)")

    assertValue("((fn [[x & y]] x) (list 1 2))", "1")
    assertValue("((fn [[x & y]] y) (list 1 2))", "(2)")

    assertValue("((fn [[x y & z]] x) (list 1 2 3 4))", "1")
    assertValue("((fn [[x y & z]] y) (list 1 2 3 4))", "2")
    assertValue("((fn [[x y & z]] z) (list 1 2 3 4))", "(3 4)")

    assertError("((fn [[&]]))")
    assertError("((fn [[&]]) 1)")

    assertError("((fn [[&]]) (list 1))")
    assertError("((fn [[x & y]]) (list))")
  }

  @Test
  def underscore(): Unit = {
    assertValue("((fn [[_ y]] y) (list 1 2))", "2")

    assertError("((fn [[_]] _) (list 1))")
  }

  @Test
  def combination(): Unit = {
    assertValue("((fn [a [b & c]] (list a b c)) 1 (list 2 3 4))", "(1 2 (3 4))")
    assertValue("((fn [[a & b] c] (list a b c)) (list 1 2 3) 4)", "(1 (2 3) 4)")
    assertValue("((fn [[a & b] [c & d]] (list a b c d)) (list 1 2 3) (list 4 5 6))", "(1 (2 3) 4 (5 6))")

    assertValue("((fn [a & [b & c]] (list a b c)) 1 2 3 4)", "(1 2 (3 4))")
    assertValue("((fn [[a & b] & c] (list a b c)) (list 1 2 3) 4)", "(1 (2 3) (4))")
    assertValue("((fn [[a & b] & [c & d]] (list a b c d)) (list 1 2 3) 4 5 6)", "(1 (2 3) 4 (5 6))")
  }
}