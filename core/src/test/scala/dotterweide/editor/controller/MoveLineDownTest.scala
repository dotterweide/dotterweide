/*
 *  MoveLineDownTest.scala
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

class MoveLineDownTest extends ActionTestBase((d, t) => new MoveLineDown(d, t)(Platform())) {
  @Test
  def line(): Unit = {
    assertEffectIs("|foo\nbar", "bar\n|foo")

    assertEffectIs("|foo\nbar\n", "bar\n|foo\n")
    assertEffectIs("|foo\nbar\nmoo", "bar\n|foo\nmoo")

    assertEffectIs("\n|foo\nbar", "\nbar\n|foo")
    assertEffectIs("moo\n|foo\nbar", "moo\nbar\n|foo")

    assertEffectIs("\n|foo\nbar\n", "\nbar\n|foo\n")
    assertEffectIs("moo\n|foo\nbar\nmoo", "moo\nbar\n|foo\nmoo")

    assertEffectIs("|\nfoo", "foo\n|")
    assertEffectIs("|foo\n", "\n|foo")
  }
}