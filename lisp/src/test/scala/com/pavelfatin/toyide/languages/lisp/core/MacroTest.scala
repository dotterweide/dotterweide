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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class MacroTest extends InterpreterTesting {
  @Test
  def eval(): Unit = {
    assertValue("(eval 1)", "1")

    assertValue("(eval (quote (+ 1 2)))", "3")

    assertOutput("(eval (quote (print 1)))", "1")

    assertValue("(let [x 1] (eval x))", "1")

    assertError("(eval)")
    assertError("(eval 1 2)")
  }

  @Test
  def macroFunction(): Unit = {
    assertValue("(macro [])", "macro0")
    assertValue("(macro [x])", "macro1")
    assertValue("(macro [x y])", "macro2")
    assertValue("(macro [] 1)", "macro0")
    assertValue("(macro [] 1 2)", "macro0")

    assertValue("(macro name [x y] 1 2)", "name_macro2")

    assertError("(macro)")
    assertError("(macro 1)")
    assertError("(macro 1 [])")
    assertError("(macro name)")
    assertError("(macro name 1)")

    assertValue("macro", "core.macro")
    assertValue("(def m macro) (m [x])", "macro1")
  }

  @Test
  def macroExpand(): Unit = {
    assertValue("(macroexpand (quote ((macro [x] (list (quote print) x)) (+ 1 2))))", "(print (+ 1 2))")

    assertError("(macroexpand)")
    assertError("(macroexpand 1)")
    assertError("(macroexpand 1 2)")
  }
}