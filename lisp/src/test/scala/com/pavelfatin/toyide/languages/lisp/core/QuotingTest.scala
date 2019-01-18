/*
 *  QuotingTest.scala
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

class QuotingTest extends InterpreterTesting {
  @Test
  def quote(): Unit = {
    assertValue("(quote 1)", "1")
    assertValue("(quote true)", "true")
    assertValue("(quote \\c)", "\\c")

    assertValue("(quote symbol)", "symbol")

    assertValue("(quote ())", "()")
    assertValue("(quote (symbol))", "(symbol)")
    assertValue("(quote (symbol 1 2 3))", "(symbol 1 2 3)")
    assertValue("(quote (symbol (1 2 3)))", "(symbol (1 2 3))")

    assertValue("(quote (print 1))", "(print 1)")
    assertOutput("(quote (print 1))", "")

    assertValue("quote", "core.quote")
    assertValue("(def f quote) (f symbol)", "symbol")

    assertError("(quote)")
    assertError("(quote 1 2)")
  }

  @Test
  def unquote(): Unit = {
    assertValue("unquote", "core.unquote")

    assertError("(unquote)")
    assertError("(unquote 1)")
    assertError("(unquote 1 2)")
  }

  @Test
  def quasiquote(): Unit = {
    assertValue("(quasiquote 1)", "1")
    assertValue("(quasiquote true)", "true")
    assertValue("(quasiquote \\c)", "\\c")

    assertValue("(quasiquote symbol)", "symbol")

    assertValue("(quasiquote ())", "()")
    assertValue("(quasiquote (symbol))", "(symbol)")
    assertValue("(quasiquote (symbol 1 2 3))", "(symbol 1 2 3)")
    assertValue("(quasiquote (symbol (1 2 3)))", "(symbol (1 2 3))")

    assertValue("(quasiquote (print 1))", "(print 1)")
    assertOutput("(quasiquote (print 1))", "")

    assertValue("quasiquote", "core.quasiquote")
    assertValue("(def f quasiquote) (f symbol)", "symbol")

    assertError("(quasiquote)")
    assertError("(quasiquote 1 2)")
  }

  @Test
  def unquoting(): Unit = {
    assertValue("(quasiquote (unquote 1))", "1")
    assertValue("(quasiquote (unquote (+ 1 2)))", "3")

    assertValue("(quasiquote (symbol (+ 1 2)))", "(symbol (+ 1 2))")
    assertValue("(quasiquote (symbol (unquote (+ 1 2))))", "(symbol 3)")
  }

  @Test
  def unquoteSplicing(): Unit = {
    assertValue("(quasiquote ((list 1 2)))", "((list 1 2))")
    assertValue("(quasiquote ((unquote-splicing (list 1 2))))", "(1 2)")

    assertValue("(quasiquote (symbol (list 1 2)))", "(symbol (list 1 2))")
    assertValue("(quasiquote (symbol (unquote-splicing (list 1 2))))", "(symbol 1 2)")

    assertError("(quasiquote (unquote-splicing (list 1 2)))")
    assertError("(quasiquote (symbol (unquote-splicing 1)))")
  }

  @Test
  def uniqueSymbolGeneration(): Unit = {
    assertValue("(gensym \"a\")", "a_0")

    assertValue("(list (gensym \"a\") (gensym \"a\"))", "(a_0 a_1)")

    assertValue("(list (gensym \"a\") (gensym \"b\"))", "(a_0 b_1)")
  }

  @Test
  def autoUniqueSymbolGeneration(): Unit = {
    assertValue("(quasiquote a#)", "a_0")
    assertValue("(quasiquote (a#))", "(a_0)")
    assertValue("(quasiquote (a# a#))", "(a_0 a_0)")
    assertValue("(quasiquote (a# (a#)))", "(a_0 (a_0))")
    assertValue("(quasiquote ((a#) a#))", "((a_0) a_0)")
    assertValue("(quasiquote ((a#) (a#)))", "((a_0) (a_0))")

    assertValue("(quasiquote (a# b#))", "(a_0 b_1)")

    assertValue("(list (quasiquote a#) (quasiquote a#))", "(a_0 a_1)")

    assertValue("(quasiquote a)", "a")

    assertValue("(quote a#)", "a#")
  }
}