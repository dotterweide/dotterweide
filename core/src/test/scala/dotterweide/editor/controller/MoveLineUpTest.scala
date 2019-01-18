/*
 *  MoveLineUpTest.scala
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

import org.junit.Test

class MoveLineUpTest extends ActionTestBase(new MoveLineUp(_, _)) {
  @Test
  def line(): Unit = {
    assertEffectIs("foo\n|bar", "|bar\nfoo")

    assertEffectIs("foo\n|bar\n", "|bar\nfoo\n")
    assertEffectIs("foo\n|bar\nmoo", "|bar\nfoo\nmoo")

    assertEffectIs("\nfoo\n|bar", "\n|bar\nfoo")
    assertEffectIs("moo\nfoo\n|bar", "moo\n|bar\nfoo")

    assertEffectIs("\nfoo\n|bar\n", "\n|bar\nfoo\n")
    assertEffectIs("moo\nfoo\n|bar\nmoo", "moo\n|bar\nfoo\nmoo")

    assertEffectIs("\n|foo", "|foo\n")
    assertEffectIs("foo\n|", "|\nfoo")
  }
}