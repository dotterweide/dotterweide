/*
 *  WebServerTest.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

/*
 * Original code copyright 2018 Pavel Fatin, https://pavelfatin.com
 * Licensed under the Apache License, Version 2.0 (the "License"): http://www.apache.org/licenses/LICENSE-2.0
 */

package dotterweide.languages.lisp.examples

import dotterweide.languages.lisp.library.Library
import dotterweide.languages.lisp.value.Environment
import dotterweide.languages.lisp.{InterpreterTesting, LispExamples}
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
