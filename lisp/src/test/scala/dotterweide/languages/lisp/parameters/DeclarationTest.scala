/*
 *  DeclarationTest.scala
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

class DeclarationTest extends InterpreterTesting {
  @Test
  def singular(): Unit = {
    assertOK("(fn [])")
    assertOK("(fn [x])")
    assertOK("(fn [x y])")

    assertError("(fn)")
    assertError("(fn 1)")
    assertError("(fn [1])")
  }

  @Test
  def plural(): Unit = {
    assertOK("(fn [&])")
    assertOK("(fn [x &])")
    assertOK("(fn [& x])")
    assertOK("(fn [x & y])")
    assertOK("(fn [x y & z])")

    assertError("(fn [& &])")
    assertError("(fn [x & &])")
    assertError("(fn [& x &])")
    assertError("(fn [& & x])")
    assertError("(fn [x & y &])")
  }

  @Test
  def duplicates(): Unit = {
    assertError("(fn [x x])")
    assertError("(fn [x & x])")
  }

  @Test
  def underscore(): Unit = {
    assertOK("(fn [_ _])")
    assertOK("(fn [_ & _])")
  }
}