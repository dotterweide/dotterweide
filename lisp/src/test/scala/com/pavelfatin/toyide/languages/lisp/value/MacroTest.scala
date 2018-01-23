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

class MacroTest extends InterpreterTesting {
  @Test
  def presentation(): Unit = {
    assertValue("(macro [] 1)", "macro0")
    assertValue("(macro [x] 1)", "macro1")
    assertValue("(macro [x y] 1)", "macro2")

    assertValue("(macro [&])", "macro0")
    assertValue("(macro [& l])", "macro0*")
    assertValue("(macro [x & l])", "macro1*")
    assertValue("(macro [x y & l])", "macro2*")

    assertValue("(macro named [])", "named_macro0")
  }

  @Test
  def evaluation(): Unit = {
    assertError("(eval (macro []))")
  }

  @Test
  def application(): Unit = {
    assertValue("((macro [] 1))", "1")
    assertValue("((macro [] 1 2))", "2")

    assertOutput("((macro [] (trace 1) (trace 2)))", "12")
    assertOutput("((macro [x] 1) (trace 2))", "")
    assertOutput("((macro [x] x 2) (trace 2))", "")
  }
}