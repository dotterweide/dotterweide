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

class TypeTest extends InterpreterTesting {
  @Test
  def integer(): Unit = {
    assertValue("(integer? 1)", "true")
    assertValue("(integer? \\с)", "false")
  }

  @Test
  def boolean(): Unit = {
    assertValue("(boolean? false)", "true")
    assertValue("(boolean? 1)", "false")
  }

  @Test
  def character(): Unit = {
    assertValue("(character? \\c)", "true")
    assertValue("(character? 1)", "false")
  }

  @Test
  def symbol(): Unit = {
    assertValue("(symbol? 'do)", "true")
    assertValue("(symbol? 1)", "false")
  }

  @Test
  def function(): Unit = {
    assertValue("(function? do)", "true")
    assertValue("(function? 1)", "false")
  }

  @Test
  def list(): Unit = {
    assertValue("(list? (list))", "true")
    assertValue("(list? 1)", "false")
  }
}