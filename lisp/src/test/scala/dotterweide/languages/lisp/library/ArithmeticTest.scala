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

package dotterweide.languages.lisp.library

import org.junit.Test

class ArithmeticTest extends LibraryTestBase {
  @Test
  def inc(): Unit = {
    assertValue("(inc 3)", "4")
  }

  @Test
  def dec(): Unit = {
    assertValue("(dec 3)", "2")
  }

  @Test
  def even(): Unit = {
    assertValue("(even? 1)", "false")
    assertValue("(even? 2)", "true")
  }

  @Test
  def odd(): Unit = {
    assertValue("(odd? 1)", "true")
    assertValue("(odd? 2)", "false")
  }

  @Test
  def zero(): Unit = {
    assertValue("(zero? 0)", "true")
    assertValue("(zero? 1)", "false")
  }

  @Test
  def pos(): Unit = {
    assertValue("(pos? 1)", "true")
    assertValue("(pos? 0)", "false")
    assertValue("(pos? -1)", "false")
  }

  @Test
  def neg(): Unit = {
    assertValue("(neg? -1)", "true")
    assertValue("(neg? 0)", "false")
    assertValue("(neg? 1)", "false")
  }

  @Test
  def sum(): Unit = {
    assertValue("(sum nil)", "0")

    assertValue("(sum '(1 2))", "3")
  }

  @Test
  def product(): Unit = {
    assertValue("(product '())", "1")

    assertValue("(product '(2 3))", "6")
  }
}
