/*
 *  RemoveLineTest.scala
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

package dotterweide.editor.controller

import dotterweide.Platform
import org.junit.Test

class RemoveLineTest extends ActionTestBase((d, t) => new RemoveLine(d, t)(Platform())) {
  @Test
  def line(): Unit = {
    assertEffectIs("1\n|2\n3", "1\n|3")
    assertEffectIs("|foo", "|")
    assertEffectIs("|[foo]", "|")
    assertEffectIs("|", "|")
    assertEffectIs("foo|\n\nbar", "|\nbar")           // issue #14
    assertEffectIs("foo|\nother\nbar", "oth|er\nbar") // stay at column if possible
    assertEffectIs("foo|\not\nbar", "ot|\nbar")       // otherwise clip column to next line length
  }
}