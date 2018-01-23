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

class ArithmeticTest extends InterpreterTesting {
  @Test
  def add(): Unit = {
    assertValue("(+)", "0")

    assertValue("(+ 1)", "1")
    assertValue("(+ 1 2)", "3")
    assertValue("(+ 1 2 3)", "6")

    assertOutput("(+ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("+", "core.+")
    assertValue("(def f +) (f 1 2)", "3")

    assertError("(+ true)")
  }

  @Test
  def sub(): Unit = {
    assertValue("(- 1)", "-1")

    assertValue("(- 3 1)", "2")
    assertValue("(- 3 2 5)", "-4")

    assertOutput("(- (trace 1) (trace 2) (trace 3))", "123")

    assertValue("-", "core.-")
    assertValue("(def f -) (f 1 2)", "-1")

    assertError("(-)")
    assertError("(- true)")
  }

  @Test
  def mul(): Unit = {
    assertValue("(*)", "1")

    assertValue("(* 2)", "2")
    assertValue("(* 2 3)", "6")
    assertValue("(* 2 3 4)", "24")

    assertOutput("(* (trace 1) (trace 2) (trace 3))", "123")

    assertValue("*", "core.*")
    assertValue("(def f *) (f 2 3)", "6")

    assertError("(* true)")
  }

  @Test
  def div(): Unit = {
    assertValue("(/ 6 3)", "2")
    assertValue("(/ 6 3 2 )", "1")

    assertValue("(/ 0 1)", "0")

    assertOutput("(/ (trace 1) (trace 2) (trace 3))", "123")

    assertValue("/", "core./")
    assertValue("(def f /) (f 6 3)", "2")

    assertError("(/)")
    assertError("(/ 1 0)", "zero")
    assertError("(/ 1 0 2)", "zero")
    assertError("(/ true)")
  }

  @Test
  def mod(): Unit = {
    assertValue("(mod 5 3)", "2")
    assertValue("(mod 6 3)", "0")

    assertValue("(mod 0 1)", "0")

    assertOutput("(mod (trace 1) (trace 2))", "12")

    assertValue("mod", "core.mod")
    assertValue("(def f mod) (f 5 3)", "2")

    assertError("(mod)")
    assertError("(mod 1 2 3)")
    assertError("(mod 1 0)", "zero")
    assertError("(mod true true)")
  }
}