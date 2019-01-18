/*
 *  ListTest.scala
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

package dotterweide.languages.lisp.value

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class ListTest extends InterpreterTesting {
  @Test
  def emptyApplication(): Unit = {
    assertError("()", "Empty application")
  }

  @Test
  def applicationToValue(): Unit = {
    assertError("(1)", "Cannot apply to 1")
  }

  @Test
  def evaluationOrder(): Unit = {
    assertOutput("((trace list) (trace 1) (trace 2))", "core.list12")
  }

  @Test
  def macroCaching(): Unit = {
    val definition = "(def m (macro [x] (trace x))) "

    assertOutput(definition + "(m 1) (m 2)", "12")

    assertOutput(definition + "(def f (fn [x] (m x))) (f 1) (f 2)", "x")
  }
}