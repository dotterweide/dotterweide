/*
 *  FormatTestBase.scala
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

package dotterweide.languages.toy.format

import dotterweide.Helpers._
import dotterweide.formatter.FormatterImpl
import dotterweide.languages.toy.{ToyFormat, ToyLexer}
import dotterweide.parser.SyncParser
import org.junit.Assert._

class FormatTestBase {
  private val formatter = new FormatterImpl(ToyFormat)

  protected def assertFormatted(code: String, parser: SyncParser, expectation: String, check: Boolean = true): Unit = {
    val node = parser.parse(ToyLexer.analyze(code))
    if(check) assertNoProblemsIn(node.elements)
    val actual = formatter.format(node, None, 2)
    assertEquals(expectation, actual)
  }
}