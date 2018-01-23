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

import com.pavelfatin.toyide.interpreter.{EvaluationException, Place}
import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Assert._
import org.junit.Test

class PlaceTest extends InterpreterTesting {
  @Test
  def undefinedSymbol(): Unit = {
    assertTrace("undefined", Place(Some("Test"), 0))
  }

  @Test
  def emptyApplication(): Unit = {
    assertTrace("()", Place(Some("Test"), 0))
  }

  @Test
  def applicationToValue(): Unit = {
    assertTrace("(1)", Place(Some("Test"), 0))
  }

  @Test
  def coreFunction(): Unit = {
    assertTrace("(error)", Place(Some("Test"), 0))
  }

  @Test
  def userFunction(): Unit = {
    assertTrace("((fn []\n(error)))", Place(Some("Test"), 1), Place(Some("Test"), 0))
    assertTrace("((fn f []\n(error)))", Place(Some("Test.f"), 1), Place(Some("Test"), 0))
  }

  @Test
  def macroFunction(): Unit = {
    assertTrace("((macro []\n(error)))", Place(Some("Test"), 1), Place(Some("Test"), 0))
    assertTrace("((macro m []\n(error)))", Place(Some("Test.m"), 1), Place(Some("Test"), 0))
  }

  @Test
  def functionLiteral(): Unit = {
    assertTrace("(\n#(error))", Place(Some("Test"), 1), Place(Some("Test"), 0))
  }

  @Test
  def listInsideFunctionLiteral(): Unit = {
    assertTrace("(#(\n(error)))", Place(Some("Test"), 1), Place(Some("Test"), 0))
  }

  @Test
  def apply(): Unit = {
    assertTrace("(apply error\n(list))", Place(Some("Test"), 0))
  }

  @Test
  def quote(): Unit = {
    assertTrace("(eval\n'(error))", Place(Some("Test"), 1), Place(Some("Test"), 0))
  }

  @Test
  def quasiQuote(): Unit = {
    assertTrace("(eval\n`(error))", Place(Some("Test"), 1), Place(Some("Test"), 0))
  }

  @Test
  def multiple(): Unit = {
    val code = "(def f (fn [] (error)))\n(def g (fn [] (f)))\n(g)"
    assertTrace(code, Place(Some("Test"), 0), Place(Some("Test"), 1), Place(Some("Test"), 2))
  }

  @Test
  def undefinedSymbolInsideFunctionLiteral(): Unit = {
    assertTrace("(#(\nfoo))", Place(Some("Test"), 1), Place(Some("Test"), 0))
  }

  private def assertTrace(code: String, places: Place*): Unit = {
    try {
      run(code)
      fail("No stack trace")
    } catch {
      case e: EvaluationException =>
        assertEquals(places.toList, e.trace.toList)
    }
  }
}