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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.interpreter.{EvaluationException, Value}
import com.pavelfatin.toyide.languages.lisp.node.ProgramNode
import com.pavelfatin.toyide.languages.lisp.value.{Environment, EnvironmentImpl}
import org.junit.Assert._

trait InterpreterTesting {
  protected def createEnvironment(): Environment = new EnvironmentImpl()

  protected def assertOutput(code: String, expected: String): Unit =
    assertEquals(expected, run(code)._2)

  protected def assertValue(code: String, expected: String): Unit =
    assertEquals(expected, run(code)._1.presentation)

  protected def assertOK(code: String): Unit = run(code)

  protected def assertError(code: String, expected: String = ""): Unit =
    try {
      run(code)
      fail("Error expected: " + expected)
    } catch {
      case EvaluationException(message, _) =>
        assertTrue("Expected: " + expected + ", actual: " + message, message.contains(expected))
    }

  protected def run(code: String, environment: Environment = createEnvironment()): (Value, String) =
    InterpreterTesting.run(code.stripMargin, environment)
}

object InterpreterTesting {
  val Source = "Test"

  def parse(code: String): ProgramNode = {
    val root = LispParser.parse(LispLexer.analyze(code))
    val elements = root.elements
    assertNoProblemsIn(elements)
    root.asInstanceOf[ProgramNode]
  }

  def run(code: String, environment: Environment): (Value, String) = {
    val root = parse(code)
    val console = new MockConsole()
    val value = root.evaluate(Source, environment, console)
    (value, console.text)
  }
}
