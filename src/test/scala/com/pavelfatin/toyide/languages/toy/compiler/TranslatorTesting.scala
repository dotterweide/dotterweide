/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.toy.compiler

import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.MockConsole
import com.pavelfatin.toyide.compiler.{Assembler, BytecodeInvoker}
import com.pavelfatin.toyide.languages.toy.ToyLexer
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import org.junit.Assert._

trait TranslatorTesting {
  private val Name = "Main"

  protected def assertOutput(code: String, expected: String): Unit = {
    assertEquals(expected, run(code))
  }

  protected def run(code: String): String = {
    val root = ProgramParser.parse(ToyLexer.analyze(code))

    val elements = root.elements
    assertNoProblemsIn(elements)
    assertNoUnresolvedIn(elements)

    val bytecode = Assembler.assemble(root, Name)

    val output = new MockConsole()
    BytecodeInvoker.invoke(bytecode, Name, output)
    output.text
  }
}