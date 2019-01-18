/*
 *  MacroTest.scala
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

package dotterweide.languages.lisp.value

import dotterweide.languages.lisp.InterpreterTesting
import org.junit.Test

class MacroTest extends InterpreterTesting {
  @Test
  def presentation(): Unit = {
    assertValue("(macro [] 1)", "macro0")
    assertValue("(macro [x] 1)", "macro1")
    assertValue("(macro [x y] 1)", "macro2")

    assertValue("(macro [&])", "macro0")
    assertValue("(macro [& l])", "macro0*")
    assertValue("(macro [x & l])", "macro1*")
    assertValue("(macro [x y & l])", "macro2*")

    assertValue("(macro named [])", "named_macro0")
  }

  @Test
  def evaluation(): Unit = {
    assertError("(eval (macro []))")
  }

  @Test
  def application(): Unit = {
    assertValue("((macro [] 1))", "1")
    assertValue("((macro [] 1 2))", "2")

    assertOutput("((macro [] (trace 1) (trace 2)))", "12")
    assertOutput("((macro [x] 1) (trace 2))", "")
    assertOutput("((macro [x] x 2) (trace 2))", "")
  }
}