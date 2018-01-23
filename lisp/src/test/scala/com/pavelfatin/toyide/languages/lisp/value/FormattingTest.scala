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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
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