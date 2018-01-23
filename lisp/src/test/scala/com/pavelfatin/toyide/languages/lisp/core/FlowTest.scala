/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.InterpreterTesting
import org.junit.Test

class FlowTest extends InterpreterTesting {
  @Test
  def program(): Unit = {
    assertValue("", "()")
    assertValue("1", "1")
    assertValue("1 2 3", "3")

    assertOutput("(print 1) (print 2) (print 3)", "123")
  }

  @Test
  def doForm(): Unit = {
    assertValue("(do)", "()")
    assertValue("(do 1)", "1")
    assertValue("(do 1 2 3)", "3")

    assertOutput("(do (print 1) (print 2) (print 3))", "123")

    assertValue("do", "core.do")
    assertValue("(def f do) (f 1)", "1")
  }

  @Test
  def ifForm(): Unit = {
    assertValue("(if true 1)", "1")
    assertValue("(if false 1)", "()")

    assertOutput("(if true (print 1))", "1")
    assertOutput("(if false (print 1))", "")

    assertOutput("(if (trace true) (print 1))", "true1")
  }

  @Test
  def ifElse(): Unit = {
    assertValue("(if true 1 2)", "1")
    assertValue("(if false 1 2)", "2")

    assertOutput("(if true (print 1) (print 2))", "1")
    assertOutput("(if false (print 1) (print 2))", "2")

    assertOutput("(if (trace true) (print 1) (print 2))", "true1")
  }

  @Test
  def ifCondition(): Unit = {
    assertValue("(if '() 1 2)", "2")
    assertValue("(if '(1 2 3) 1 2)", "1")

    assertValue("(if 0 1 2)", "1")
    assertValue("(if \\c 1 2)", "1")
  }

  @Test
  def ifValue(): Unit = {
    assertValue("if", "core.if")
    assertValue("(def f if) (f true 1)", "1")
  }

  @Test
  def ifErrors(): Unit = {
    assertError("(if)")
    assertError("(if true)")
    assertError("(if true 1 2 3)")
  }

  @Test
  def error(): Unit = {
    assertError("(error \"foo\")", "foo")
    assertError("(error 1)", "1")

    assertError("(error 1 2)", "1 2")

    assertError("(error)", "")

    assertValue("error", "core.error")
  }

  @Test
  def loop(): Unit = {
    assertValue("(loop [])", "()")
    assertValue("(loop [] 1)", "1")
    assertValue("(loop [] 1 2)", "2")

    assertValue("(loop [x 1] x)", "1")
    assertValue("(loop [x 1 y 2] x)", "1")
    assertValue("(loop [x 1 y 2] y)", "2")

    assertValue("(loop [x 1 y (* x 2)] y)", "2")
  }

  @Test
  def recurInLoop(): Unit = {
    assertOutput("(loop [x 1] (if (< x 4) (do (print x) (recur (+ x 1)))))", "123")

    assertOutput("(loop [x 1 y (* x 2)] (if (< x 4) (do (print y) (recur (+ x 1) (+ x 1)))))", "223")

    assertError("(loop [x 1] (recur))", "arguments")
  }

  @Test
  def recurInFunction(): Unit = {
    assertOutput("((fn [x] (if (< x 4) (do (print x) (recur (+ x 1))))) 1)", "123")

    assertOutput("((fn [x y] (if (< x 4) (do (print y) (recur (+ x 1) (+ x 1))))) 1 2)", "223")

    assertError("((fn [x] (recur)) 1)", "arguments")
  }
}