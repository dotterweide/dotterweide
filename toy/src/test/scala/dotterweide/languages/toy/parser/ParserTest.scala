/*
 *  ParserTest.scala
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

package dotterweide.languages.toy.parser

import dotterweide.languages.toy.ToyLexer
import dotterweide.lexer.Lexer
import dotterweide.parser.SyncParser
import org.junit.Assert._

abstract class ParserTest(parser: SyncParser, lexer: Lexer = ToyLexer) {
  def parsed(s: String): String =
    parser.parse(lexer.analyze(s)).content

  def assertParsed(s: String, expectation: String): Unit = {
    assertEquals(format(expectation), parsed(s).trim)
  }

  private def format(expectation: String) = {
    val content = expectation.replace("\r\n", "\n")
      .replaceFirst("^ *\n", "").replaceFirst("\\s+$", "")

    val indent = content.takeWhile(_ == ' ').length

    content.split('\n').map(_.drop(indent)).mkString("\n")
  }
}