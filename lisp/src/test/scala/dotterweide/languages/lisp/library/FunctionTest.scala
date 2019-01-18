/*
 *  FunctionTest.scala
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

package dotterweide.languages.lisp.library

import org.junit.Test

class FunctionTest extends LibraryTestBase {
   @Test
   def apply(): Unit = {
     assertValue("(apply + '(1 2))", "3")

     assertOutput("(apply print '((1 2) (3 4)))", "(1 2) (3 4)")
   }

  @Test
  def applyMacro(): Unit = {
    assertValue("(apply (macro [& l] (cons 'list l)) '(1 2))", "(1 2)")

    assertValue("(apply (macro [& l] (cons 'list l)) '((list 1 2) (list 3 4)))", "((1 2) (3 4))")
  }

  @Test
   def identity(): Unit = {
     assertValue("(identity 1)", "1")
   }

  @Test
  def const(): Unit = {
    assertValue("((const 1) 2)", "1")
  }

   @Test
   def comp(): Unit = {
     assertValue("((comp) 4)", "4")
     assertValue("((comp (fn [x] (* x 2))) 4)", "8")
     assertValue("((comp (fn [x] (* x 2)) (fn [x] (- x 1))) 4)", "6")
   }

   @Test
   def partial(): Unit = {
     assertValue("((partial + 1 2) 4 5)", "12")
   }

   @Test
   def complement(): Unit = {
     assertValue("((complement (fn [x] x)) true)", "false")
     assertValue("((complement (fn [x] x)) false)", "true")
   }
 }
