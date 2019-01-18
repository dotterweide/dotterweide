/*
 *  ReferenceTest.scala
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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Helpers._
import com.pavelfatin.toyide.languages.lisp.{LispLexer, LispParser}
import com.pavelfatin.toyide.node.{Node, ReferenceNode}
import org.junit.Test

class ReferenceTest {
  @Test
  def unknown(): Unit = {
    assertMatches(targetIn("x")) {
      case None =>
    }
  }

  @Test
  def fn(): Unit = {
    assertMatches(targetIn("(fn [x])")) {
      case None =>
    }
    assertMatches(targetIn("(fn [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(fn [x] x)")) {
      case Some(Offset(5)) =>
    }
    assertMatches(targetIn("(fn [x y] y)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(fn [[x]] x)")) {
      case Some(Offset(6)) =>
    }
    assertMatches(targetIn("(fn name [x] x)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(fn name [] name)")) {
      case None =>
    }
    assertMatches(targetIn("(fn name []) name")) {
      case None =>
    }
  }

  @Test
  def macroFn(): Unit = {
    assertMatches(targetIn("(macro [x])")) {
      case None =>
    }
    assertMatches(targetIn("(macro [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(macro [x] x)")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("(macro [x y] y)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(macro [[x]] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(macro name [x] x)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(macro name [] name)")) {
      case None =>
    }
    assertMatches(targetIn("(macro name []) name")) {
      case None =>
    }
  }

  @Test
  def loop(): Unit = {
    assertMatches(targetIn("(loop [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(loop [x] x)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(loop [x 1 y 2] y)")) {
      case Some(Offset(11)) =>
    }
    assertMatches(targetIn("(loop [[x] '(1)] x)")) {
      case Some(Offset(8)) =>
    }
    assertMatches(targetIn("(loop [x x])")) {
      case Some(Offset(7)) =>
    }
  }

  @Test
  def let(): Unit = {
    assertMatches(targetIn("(let [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(let [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(let [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(let [x 1] x)")) {
      case Some(Offset(6)) =>
    }
    assertMatches(targetIn("(let [x 1 y 2] y)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(let [[x] '(1)] x)")) {
      case Some(Offset(7)) =>
    }
    assertMatches(targetIn("(let [x x])")) {
      case Some(Offset(6)) =>
    }
  }

  @Test
  def ifLet(): Unit = {
    assertMatches(targetIn("(if-let [x 1])")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x 1]) x")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x y]) y")) {
      case None =>
    }
    assertMatches(targetIn("(if-let [x 1] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(if-let [x 1 y 2] y)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(if-let [[x] '(1)] x)")) {
      case Some(Offset(10)) =>
    }
    assertMatches(targetIn("(if-let [x x])")) {
      case Some(Offset(9)) =>
    }
  }

  @Test
  def define(): Unit = {
    assertMatches(targetIn("(def x 1)")) {
      case None =>
    }
    assertMatches(targetIn("(def x x)")) {
      case Some(Offset(5)) =>
    }
    assertMatches(targetIn("(def x 1) x")) {
      case Some(Offset(5)) =>
    }
  }

  @Test
  def defn(): Unit = {
    assertMatches(targetIn("(defn f [])")) {
      case None =>
    }
    assertMatches(targetIn("(defn f []) f")) {
      case Some(Offset(6)) =>
    }

    assertMatches(targetIn("(defn f [x])")) {
      case None =>
    }
    assertMatches(targetIn("(defn f [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(defn f [x] x)")) {
      case Some(Offset(9)) =>
    }
    assertMatches(targetIn("(defn f [x y] y)")) {
      case Some(Offset(11)) =>
    }
    assertMatches(targetIn("(defn f [[x]] x)")) {
      case Some(Offset(10)) =>
    }
  }

  @Test
  def defmacro(): Unit = {
    assertMatches(targetIn("(defmacro m [])")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m []) m")) {
      case Some(Offset(10)) =>
    }

    assertMatches(targetIn("(defmacro m [x])")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m [x]) x")) {
      case None =>
    }
    assertMatches(targetIn("(defmacro m [x] x)")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(defmacro m [x y] y)")) {
      case Some(Offset(15)) =>
    }
    assertMatches(targetIn("(defmacro m [[x]] x)")) {
      case Some(Offset(14)) =>
    }
  }

  @Test
  def precedence(): Unit = {
    assertMatches(targetIn("(fn [x] (fn [x] x))")) {
      case Some(Offset(13)) =>
    }
    assertMatches(targetIn("(def x 1) (fn [x] x)")) {
      case Some(Offset(15)) =>
    }
    assertMatches(targetIn("(def x 1) (def x 1) x")) {
      case Some(Offset(5)) =>
    }
  }

  private def referenceIn(code: String): Option[ReferenceNode] = {
    val elements = LispParser.parse(LispLexer.analyze(code)).elements
    assertNoProblemsIn(elements)
    elements.filterBy[SymbolNode].find(_.target.isDefined)
  }

  private def targetIn(code: String): Option[Node] = {
    referenceIn(code).flatMap(_.target)
  }
}