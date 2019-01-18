/*
 *  ConsoleTest.scala
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

class ConsoleTest extends InterpreterTesting {
  @Test
  def empty(): Unit = {
    assertOutput("", "")
    assertOutput("1", "")
  }

  @Test
  def print(): Unit = {
    assertOutput("(print)", "")
    assertOutput("(print 1 2 3)", "1 2 3")

    assertValue("(print 1)", "()")

    assertValue("print", "core.print")
    assertOutput("(def f print) (f 1)", "1")
  }

  @Test
  def println(): Unit = {
    assertOutput("(println)", "\n")
    assertOutput("(println 1 2 3)", "1 2 3\n")

    assertValue("(println 1)", "()")

    assertValue("println", "core.println")
    assertOutput("(def f println) (f 1)", "1\n")
  }

  @Test
  def trace(): Unit = {
    assertValue("(trace 1)", "1")
    assertValue("(trace true)", "true")
    assertValue("(trace \\c)", "\\c")
    assertValue("(trace (list 1 2 3))", "(1 2 3)")

    assertOutput("(trace 1)", "1")
    assertOutput("(trace true)", "true")
    assertOutput("(trace \\c)", "\\c")
    assertOutput("(trace (list 1 2 3))", "(1 2 3)")

    assertValue("trace", "core.trace")
    assertOutput("(def f trace) (f 1)", "1")

    assertError("(trace)")
    assertError("(trace 1 2)")
  }

  @Test
  def format(): Unit = {
    assertValue("(format 123)", "(\\1 \\2 \\3)")
    assertOutput("(format 123)", "")
  }
}