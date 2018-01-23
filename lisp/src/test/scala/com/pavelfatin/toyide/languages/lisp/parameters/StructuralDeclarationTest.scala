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

class StructuralDeclarationTest extends InterpreterTesting {
  @Test
  def singular(): Unit = {
    assertOK("(fn [[]])")
    assertOK("(fn [[x]])")
    assertOK("(fn [[x y]])")

    assertError("(fn [[1]])")
  }

  @Test
  def plural(): Unit = {
    assertOK("(fn [[&]])")
    assertOK("(fn [[x &]])")
    assertOK("(fn [[& x]])")
    assertOK("(fn [[x & y]])")
    assertOK("(fn [[x y & z]])")

    assertError("(fn [[& &]])")
    assertError("(fn [[x & &]])")
    assertError("(fn [[& x &]])")
    assertError("(fn [[& & x]])")
    assertError("(fn [[x & y &]])")
  }

  @Test
  def duplicates(): Unit = {
    assertError("(fn [[x x]])")
    assertError("(fn [[x & x]])")
    assertError("(fn [[x] [x]])")
  }

  @Test
  def underscore(): Unit = {
    assertOK("(fn [[_ _]])")
    assertOK("(fn [[_ & _]])")
    assertOK("(fn [[_] [_]])")
  }

  @Test
  def combination(): Unit = {
    assertOK("(fn [a [b & c]])")
    assertOK("(fn [[a & b] c])")
    assertOK("(fn [[a & b] [c & d]])")

    assertOK("(fn [a & [b & c]])")
    assertOK("(fn [[a & b] & c])")
    assertOK("(fn [[a & b] & [c & d]])")
  }
}