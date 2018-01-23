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