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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class BindingTest extends InterpreterTesting {
  @Test
  def singular(): Unit = {
    assertValue("((fn []))", "()")

    assertValue("((fn [x] x) 1)", "1")
    assertValue("((fn [x y] x) 1 2)", "1")
    assertValue("((fn [x y] y) 1 2)", "2")

    assertError("((fn []) 1)")
    assertError("((fn [x]) 1 2)")

    assertError("((fn [x]))")
    assertError("((fn [x y]) 1)")
  }

  @Test
  def plural(): Unit = {
    assertValue("((fn [&]))", "()")

    assertValue("((fn [& x] x))", "()")
    assertValue("((fn [& x] x) 1)", "(1)")
    assertValue("((fn [& x] x) 1 2)", "(1 2)")

    assertValue("((fn [x & y] x) 1 2)", "1")
    assertValue("((fn [x & y] y) 1 2)", "(2)")

    assertValue("((fn [x y & z] x) 1 2 3 4)", "1")
    assertValue("((fn [x y & z] y) 1 2 3 4)", "2")
    assertValue("((fn [x y & z] z) 1 2 3 4)", "(3 4)")

    assertError("((fn [&]) 1)")
    assertError("((fn [x & y]))")
  }

  @Test
  def underscore(): Unit = {
    assertValue("((fn [_ y] y) 1 2)", "2")

    assertError("((fn [_] _) 1)")
  }
}