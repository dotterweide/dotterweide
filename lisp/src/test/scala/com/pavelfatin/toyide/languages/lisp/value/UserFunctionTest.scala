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

class UserFunctionTest extends InterpreterTesting {
  @Test
  def presentation(): Unit = {
    assertValue("(fn [])", "fn0")
    assertValue("(fn [x])", "fn1")
    assertValue("(fn [x y])", "fn2")

    assertValue("(fn [&])", "fn0")
    assertValue("(fn [& l])", "fn0*")
    assertValue("(fn [x & l])", "fn1*")
    assertValue("(fn [x y & l])", "fn2*")

    assertValue("(fn named [])", "named_fn0")
  }

  @Test
  def evaluation(): Unit = {
    assertError("(eval (fn []))")
  }

  @Test
  def application(): Unit = {
    assertValue("((fn []))", "()")
    assertValue("((fn [] 1))", "1")
    assertValue("((fn [] 1 2))", "2")

    assertOutput("((fn [] (trace 1) (trace 2)))", "12")
    assertOutput("((fn [x y]) (trace 1) (trace 2))", "12")
    assertOutput("((fn [x] (trace 2)) (trace 1))", "12")

    assertOutput("((fn [x] x x) (trace 1))", "1")
  }
}