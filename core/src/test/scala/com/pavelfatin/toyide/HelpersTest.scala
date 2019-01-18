/*
 *  HelpersTest.scala
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

package com.pavelfatin.toyide

import org.junit.Test
import org.junit.Assert._

class HelpersTest {
  @Test
  def parseAndFormat(): Unit = {
    val code = "[so|me] code"

    val (document, view) = Helpers.parseDocument(code)
    assertEquals("some code", document.text)
    assertEquals(2, view.offset)
    assertEquals(Some(Interval(0, 4)), view.selection)

    val text = Helpers.formatDocument(document, view)
    assertEquals(code, text)
  }
}