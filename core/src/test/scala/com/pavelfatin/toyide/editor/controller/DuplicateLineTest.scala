/*
 *  DuplicateLineTest.scala
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

class DuplicateLineTest extends ActionTestBase(new DuplicateLine(_, _)) {
  @Test
  def line(): Unit = {
    assertEffectIs("|", "\n|")
    assertEffectIs("f|oo", "foo\nf|oo")
    assertEffectIs("foo|", "foo\nfoo|")
    assertEffectIs("fo|o\nbar", "foo\nfo|o\nbar")
  }

  @Test
  def selection(): Unit = {
    assertEffectIs("[fo|o]", "foo[fo|o]")
    assertEffectIs("[|foo]", "foo[|foo]")
    assertEffectIs("[foo|]", "foo[foo|]")
    assertEffectIs("moo[fo|o]bar", "moofoo[fo|o]bar")
    assertEffectIs("[|]", "[|]")
  }
}