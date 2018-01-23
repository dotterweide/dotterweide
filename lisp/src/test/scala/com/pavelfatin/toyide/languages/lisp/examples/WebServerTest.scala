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

package com.pavelfatin.toyide.languages.lisp.examples

import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.value.Environment
import com.pavelfatin.toyide.languages.lisp.{InterpreterTesting, LispExamples}
import org.junit.Assert._
import org.junit.{Before, Test}

class WebServerTest extends InterpreterTesting {
  private val RequestTemplate = load("/RequestTemplate.txt")

  private val Code = LispExamples.WebServerCode
    .replace("./doc", "./license")
    .replace("(listen port handle-connection)", "(handle-connection mock-socket)")

  private val handle = new MockHandle()

  override def createEnvironment(): Environment = Library.instance.createEnvironment()
    .addLocals(Map("mock-socket" -> handle))

  @Before
  def resetHandle(): Unit = {
    handle.reset()
  }

  @Test
  def normal(): Unit = {
    assertResponse("/scala-license.txt", load("/NormalResponse.txt"))
  }

  @Test
  def notFound(): Unit = {
    assertResponse("/unknown.html", load("/NotFoundResponse.txt"))
  }

  @Test
  def index(): Unit = {
    assertResponse("/", load("/IndexResponse.txt"))
  }

  private def assertResponse(uri: String, expected: String): Unit = {
    handle.input = RequestTemplate.format(uri)
    run(Code)
    assertEquals(expected, handle.output)
    assertTrue(handle.closed)
  }

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }
}
