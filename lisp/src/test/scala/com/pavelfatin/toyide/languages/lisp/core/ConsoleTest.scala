/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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