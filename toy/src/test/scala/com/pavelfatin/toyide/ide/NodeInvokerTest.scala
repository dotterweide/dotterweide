/*
 *  NodeInvokerTest.scala
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

package com.pavelfatin.toyide.ide

import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.languages.toy.{ToyExamples, ToyLexer}
import org.junit.{Assert, Test}

class NodeInvokerTest {
  @Test
  def output(): Unit = {
    Assert.assertEquals("""Started:
233168
Finished (n ms)""".filter(_ != '\r'), run(ToyExamples.Euler1).replaceFirst("\\d+ ms", "n ms"))
  }

  @Test
  def translationException(): Unit = {
    Assert.assertEquals("Compilation error.\nInitializer expression not found: var a: integer = ;".filter(_ != '\r'),
      run("var a: integer = ;"))
  }

  @Test
  def invocationException(): Unit = {
    Assert.assertEquals("""Started:

java.lang.ArithmeticException: / by zero
  at c:5
  at b:9
  at a:13
  at 16""".filter(_ != '\r'), run(ToyExamples.Exception))
  }

  private def run(code: String): String = {
    val console = new MockConsole()
    val interpreter = new NodeInvoker(console)
    interpreter.run(ProgramParser.parse(ToyLexer.analyze(code)))
    val text = console.text
    text
  }
}