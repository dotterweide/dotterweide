/*
 *  IndentSelectionTest.scala
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

package com.pavelfatin.toyide.editor.controller

import org.junit.Test

class IndentSelectionTest extends ActionTestBase(new IndentSelection(_, _, 2)) {
  @Test
  def singleLine(): Unit = {
    assertEffectIs("[foo|]", "[  foo|]")
    assertEffectIs("[foo]ba|r", "[  foo]ba|r")
  }

  @Test
  def multipleLines(): Unit = {
    assertEffectIs("[foo\nbar|]", "[  foo\n  bar|]")
  }

  @Test
  def tailLine(): Unit = {
    assertEffectIs("[foo\nbar\n|]", "[  foo\n  bar\n|]")
  }
}