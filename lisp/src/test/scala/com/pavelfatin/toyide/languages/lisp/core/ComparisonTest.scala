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

class ComparisonTest extends InterpreterTesting {
  @Test
  def gt(): Unit = {
    assertValue("(> 1 2)", "false")
    assertValue("(> 2 1)", "true")
    assertValue("(> 1 1)", "false")

    assertOutput("(> (trace 1) (trace 2))", "12")

    assertValue(">", "core.>")
    assertValue("(def f >) (f 1 2)", "false")

    assertError("(> 1 true)")
    assertError("(> true 2)")
    assertError("(> true true)")
    assertError("(>)")
    assertError("(> 1)")
    assertError("(> 1 2 3)")
  }

  @Test
  def gtEq(): Unit = {
    assertValue("(>= 1 2)", "false")
    assertValue("(>= 2 1)", "true")
    assertValue("(>= 1 1)", "true")

    assertOutput("(>= (trace 1) (trace 2))", "12")

    assertValue(">=", "core.>=")
    assertValue("(def f >=) (f 1 2)", "false")

    assertError("(>= 1 true)")
    assertError("(>= true 2)")
    assertError("(>= true true)")
    assertError("(>=)")
    assertError("(>= 1)")
    assertError("(>= 1 2 3)")
  }

  @Test
  def lt(): Unit = {
    assertValue("(< 1 2)", "true")
    assertValue("(< 2 1)", "false")
    assertValue("(< 1 1)", "false")

    assertOutput("(< (trace 1) (trace 2))", "12")

    assertValue("<", "core.<")
    assertValue("(def f <) (f 1 2)", "true")

    assertError("(< 1 true)")
    assertError("(< true 2)")
    assertError("(< true true)")
    assertError("(<)")
    assertError("(< 1)")
    assertError("(< 1 2 3)")
  }

  @Test
  def ltEq(): Unit = {
    assertValue("(<= 1 2)", "true")
    assertValue("(<= 2 1)", "false")
    assertValue("(<= 1 1)", "true")

    assertOutput("(<= (trace 1) (trace 2))", "12")

    assertValue("<=", "core.<=")
    assertValue("(def f <=) (f 1 2)", "true")

    assertError("(<= 1 true)")
    assertError("(<= true 2)")
    assertError("(<= true true)")
    assertError("(<=)")
    assertError("(<= 1)")
    assertError("(<= 1 2 3)")
  }

  @Test
  def eq(): Unit = {
    assertValue("(= 1 2)", "false")
    assertValue("(= 2 1)", "false")
    assertValue("(= 1 1)", "true")

    assertValue("(= false true)", "false")
    assertValue("(= true false)", "false")
    assertValue("(= true true)", "true")
    assertValue("(= false false)", "true")

    assertValue("(= \\a \\b)", "false")
    assertValue("(= \\a \\a)", "true")

    assertValue("(= '() '())", "true")
    assertValue("(= '() '(1))", "false")
    assertValue("(= '(1) '(1))", "true")
    assertValue("(= '(1) '(2))", "false")
    assertValue("(= '(1) '(1 2))", "false")
    assertValue("(= '(1 2) '(1))", "false")
    assertValue("(= '(1 2) '(1 2))", "true")

    assertValue("(= '(1 (2 3)) '(1 (2 3)))", "true")
    assertValue("(= '(1 (2 3)) '(1 (2 4)))", "false")

    assertOutput("(= (trace 1) (trace 2))", "12")

    assertValue("=", "core.=")
    assertValue("(def f =) (f 1 2)", "false")

    assertError("(= 1 true)")
    assertError("(= true 2)")
    assertError("(= '(1) '(true))")
    assertError("(=)")
    assertError("(= 1)")
    assertError("(= 1 2 3)")
  }
}