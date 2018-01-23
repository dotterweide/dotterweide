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

class MacroTest extends InterpreterTesting {
  @Test
  def eval(): Unit = {
    assertValue("(eval 1)", "1")

    assertValue("(eval (quote (+ 1 2)))", "3")

    assertOutput("(eval (quote (print 1)))", "1")

    assertValue("(let [x 1] (eval x))", "1")

    assertError("(eval)")
    assertError("(eval 1 2)")
  }

  @Test
  def macroFunction(): Unit = {
    assertValue("(macro [])", "macro0")
    assertValue("(macro [x])", "macro1")
    assertValue("(macro [x y])", "macro2")
    assertValue("(macro [] 1)", "macro0")
    assertValue("(macro [] 1 2)", "macro0")

    assertValue("(macro name [x y] 1 2)", "name_macro2")

    assertError("(macro)")
    assertError("(macro 1)")
    assertError("(macro 1 [])")
    assertError("(macro name)")
    assertError("(macro name 1)")

    assertValue("macro", "core.macro")
    assertValue("(def m macro) (m [x])", "macro1")
  }

  @Test
  def macroExpand(): Unit = {
    assertValue("(macroexpand (quote ((macro [x] (list (quote print) x)) (+ 1 2))))", "(print (+ 1 2))")

    assertError("(macroexpand)")
    assertError("(macroexpand 1)")
    assertError("(macroexpand 1 2)")
  }
}