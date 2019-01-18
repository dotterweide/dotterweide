/*
 *  FormattingTest.scala
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

class FormattingTest extends InterpreterTesting {
  @Test
  def empty(): Unit = {
    assertOutput("(print)", "")
  }

  @Test
  def integer(): Unit = {
    assertOutput("(print 1)", "1")
  }

  @Test
  def boolean(): Unit = {
    assertOutput("(print true)", "true")
  }

  @Test
  def character(): Unit = {
    assertOutput("(print \\c)", "\\c")
    assertOutput("(print \\space)", "\\space")
    assertOutput("(print \\tab)", "\\tab")
    assertOutput("(print \\return)", "\\return")
    assertOutput("(print \\newline)", "\\newline")
  }

  @Test
  def list(): Unit = {
    assertOutput("(print (list))", "()")
    assertOutput("(print (list 1 2 3))", "(1 2 3)")
  }

  @Test
  def string(): Unit = {
    assertOutput("(print \"string\")", "string")
  }

  @Test
  def multiple(): Unit = {
    assertOutput("(print 1 2 3)", "1 2 3")
    assertOutput("(print true false true)", "true false true")
    assertOutput("(print \\a \\b \\c)", "\\a \\b \\c")
    assertOutput("(print (list 1) (list 2) (list 3))", "(1) (2) (3)")
    assertOutput("(print \"a\" \"b\" \"c\")", "a b c")

    assertOutput("(print 1 true \\a (list 2) \"b\")", "1 true \\a (2) b")
  }
}