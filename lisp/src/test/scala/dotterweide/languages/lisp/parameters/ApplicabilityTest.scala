/*
 *  ApplicabilityTest.scala
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

package dotterweide.languages.lisp.parameters

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class ApplicabilityTest extends InterpreterTesting {
   @Test
   def fnForm(): Unit = {
     assertValue("((fn [a [b & c]] (list a b c)) 1 '(2 3 4))", "(1 2 (3 4))")
   }

   @Test
   def macroForm(): Unit = {
     assertValue("((macro [a [b & c]] `(list ~a ~b (quote ~c))) 1 (2 3 4))", "(1 2 (3 4))")
   }

  @Test
  def letForm(): Unit = {
    assertValue("(let [a 1 [b & c] '(2 3 4)] (list a b c))", "(1 2 (3 4))")

    assertValue("(let [& 1] &)", "1")

    assertError("(let [_ 1] _)")
  }
}