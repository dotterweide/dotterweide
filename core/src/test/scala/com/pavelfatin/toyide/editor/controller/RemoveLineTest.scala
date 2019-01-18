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

package com.pavelfatin.toyide.editor.controller

import org.junit.Test

class RemoveLineTest extends ActionTestBase(new RemoveLine(_, _)) {
  @Test
  def line(): Unit = {
    assertEffectIs("1\n|2\n3", "1\n|3")
    assertEffectIs("|foo", "|")
    assertEffectIs("|[foo]", "|")
    assertEffectIs("|", "|")
  }
}