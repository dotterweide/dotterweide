/*
 *  ClosureTest.scala
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

class ClosureTest extends InterpreterTesting {
  @Test
  def accessibility(): Unit = {
    assertValue("((let [x 1] (fn [] x)))", "1")
    assertValue("((let [x 1] (let [y 2] (fn [] (+ x y)))))", "3")

    assertValue("((let [x 1] (macro [] x)))", "1")
    assertValue("((let [x 1] (let [y 2] (macro [] (+ x y)))))", "3")
  }

  @Test
  def priority(): Unit = {
    assertValue("((let [x 1] (fn [x] x)) 2)", "2")
    assertValue("((let [x 1] (let [x 2] (fn [] x))))", "2")
    assertValue("(let [x 1] ((let [x 2] (fn [] x))))", "2")

    assertValue("((let [x 1] (macro [x] x)) 2)", "2")
    assertValue("((let [x 1] (let [x 2] (macro [] x))))", "2")
    assertValue("(let [x 1] ((let [x 2] (macro [] x))))", "2")
  }
}