/*
 *  ProgramFormatTest.scala
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

package com.pavelfatin.toyide.languages.toy.format

import com.pavelfatin.toyide.languages.toy.parser._
import org.junit.Test

class ProgramFormatTest extends FormatTestBase {
  // statement
  // block statement
  // program
  // empty

  @Test
  def statementSeparator(): Unit = {
    assertFormatted("var a: integer = 1;var b: integer = 2;", ProgramParser,
      "var a: integer = 1; var b: integer = 2;")

    assertFormatted("var a: integer = 1; var b: integer = 2;", ProgramParser,
      "var a: integer = 1; var b: integer = 2;")

    assertFormatted("var a: integer = 1;\nvar b: integer = 2;", ProgramParser,
      "var a: integer = 1;\nvar b: integer = 2;")

    assertFormatted("var a: integer = 1;\n\nvar b: integer = 2;", ProgramParser,
      "var a: integer = 1;\n\nvar b: integer = 2;")
  }

  @Test
  def indentBraces(): Unit = {
    assertFormatted("def f(): void = {}", ProgramParser, "def f(): void = {\n}")

    assertFormatted("def f(): void = {foo();}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {foo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {\nfoo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")
    assertFormatted("def f(): void = {\n  foo();\n}", ProgramParser, "def f(): void = {\n  foo();\n}")

    assertFormatted("def f(): void = {foo();}bar();", ProgramParser, "def f(): void = {\n  foo();\n}\nbar();")
    assertFormatted("def f(): void = {foo();}\nbar();", ProgramParser, "def f(): void = {\n  foo();\n}\nbar();")

    assertFormatted("def f(): void = {foo();\nbar();}", ProgramParser, "def f(): void = {\n  foo();\n  bar();\n}")
  }

  @Test
  def indentElse(): Unit = {
    assertFormatted("if (true) {} else {}", ProgramParser, "if (true) {\n} else {\n}")
    assertFormatted("if (true) {} else {}\nfoo();", ProgramParser, "if (true) {\n} else {\n}\nfoo();")
  }

  @Test
  def nestedIndent(): Unit = {
    assertFormatted("def f(): void = {if (true) {bar();}}", ProgramParser,
      "def f(): void = {\n  if (true) {\n    bar();\n  }\n}")
  }

  @Test
  def error(): Unit = {
    assertFormatted("a=1;foo;b=2;", ProgramParser,
      "a = 1; foo; b = 2;", check = false)

    assertFormatted("var var", ProgramParser,
      "var var", check = false)
  }
}