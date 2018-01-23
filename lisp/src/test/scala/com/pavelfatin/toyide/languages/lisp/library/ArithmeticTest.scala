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

package com.pavelfatin.toyide.languages.lisp.library

import org.junit.Test

class ArithmeticTest extends LibraryTestBase {
  @Test
  def inc(): Unit = {
    assertValue("(inc 3)", "4")
  }

  @Test
  def dec(): Unit = {
    assertValue("(dec 3)", "2")
  }

  @Test
  def even(): Unit = {
    assertValue("(even? 1)", "false")
    assertValue("(even? 2)", "true")
  }

  @Test
  def odd(): Unit = {
    assertValue("(odd? 1)", "true")
    assertValue("(odd? 2)", "false")
  }

  @Test
  def zero(): Unit = {
    assertValue("(zero? 0)", "true")
    assertValue("(zero? 1)", "false")
  }

  @Test
  def pos(): Unit = {
    assertValue("(pos? 1)", "true")
    assertValue("(pos? 0)", "false")
    assertValue("(pos? -1)", "false")
  }

  @Test
  def neg(): Unit = {
    assertValue("(neg? -1)", "true")
    assertValue("(neg? 0)", "false")
    assertValue("(neg? 1)", "false")
  }

  @Test
  def sum(): Unit = {
    assertValue("(sum nil)", "0")

    assertValue("(sum '(1 2))", "3")
  }

  @Test
  def product(): Unit = {
    assertValue("(product '())", "1")

    assertValue("(product '(2 3))", "6")
  }
}
