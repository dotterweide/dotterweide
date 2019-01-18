/*
 *  TypeTest.scala
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

package dotterweide.languages.lisp.core

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class TypeTest extends InterpreterTesting {
  @Test
  def integer(): Unit = {
    assertValue("(integer? 1)", "true")
    assertValue("(integer? \\с)", "false")
  }

  @Test
  def boolean(): Unit = {
    assertValue("(boolean? false)", "true")
    assertValue("(boolean? 1)", "false")
  }

  @Test
  def character(): Unit = {
    assertValue("(character? \\c)", "true")
    assertValue("(character? 1)", "false")
  }

  @Test
  def symbol(): Unit = {
    assertValue("(symbol? 'do)", "true")
    assertValue("(symbol? 1)", "false")
  }

  @Test
  def function(): Unit = {
    assertValue("(function? do)", "true")
    assertValue("(function? 1)", "false")
  }

  @Test
  def list(): Unit = {
    assertValue("(list? (list))", "true")
    assertValue("(list? 1)", "false")
  }
}