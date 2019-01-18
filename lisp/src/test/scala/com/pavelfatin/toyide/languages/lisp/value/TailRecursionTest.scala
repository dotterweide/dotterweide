/*
 *  TailRecursionTest.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class TailRecursionTest extends InterpreterTesting {
  @Test
  def tailRecursion(): Unit = {
    val limit = (EnvironmentImpl.MaxFrames + 1).toString
    assertValue("(loop [x 1] (if (< x %s) (recur (+ x 1)) x))".format(limit), limit)
  }
}