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

class FunctionTest extends InterpreterTesting {
  @Test
  def fn(): Unit = {
    assertValue("(fn [])", "fn0")
    assertValue("(fn [x])", "fn1")
    assertValue("(fn [x y])", "fn2")
    assertValue("(fn [] 1)", "fn0")
    assertValue("(fn [] 1 2)", "fn0")

    assertValue("(fn name [x y] 1 2)", "name_fn2")

    assertError("(fn)")
    assertError("(fn 1)")
    assertError("(fn 1 [])")
    assertError("(fn name)")
    assertError("(fn name 1)")

    assertValue("fn", "core.fn")
    assertValue("(def f fn) (f [x])", "fn1")
  }

  @Test
  def apply(): Unit = {
    assertValue("(apply + '())", "0")
    assertValue("(apply + '(1))", "1")
    assertValue("(apply + '(1 2))", "3")

    assertError("(apply 1 '())")
    assertError("(apply + 1)")
    assertError("(apply + '() '())")

    assertValue("apply", "core.apply")
    assertValue("(def f apply) (f + '(1 2))", "3")
  }
}