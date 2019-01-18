/*
 *  TranslatorTesting.scala
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

package dotterweide.languages.toy.compiler

import dotterweide.Helpers._
import dotterweide.MockConsole
import dotterweide.compiler.{Assembler, BytecodeInvoker}
import dotterweide.languages.toy.ToyLexer
import dotterweide.languages.toy.parser.ProgramParser
import org.junit.Assert._

trait TranslatorTesting {
  private val Name = "Main"

  protected def assertOutput(code: String, expected: String): Unit = {
    assertEquals(expected, run(code))
  }

  protected def run(code: String): String = {
    val root = ProgramParser.parse(ToyLexer.analyze(code))

    val elements = root.elements
    assertNoProblemsIn  (elements)
    assertNoUnresolvedIn(elements)

    val bytecode = Assembler.assemble(root, Name)

    val output = new MockConsole()
    BytecodeInvoker.invoke(bytecode, Name, output)
    output.text
  }
}