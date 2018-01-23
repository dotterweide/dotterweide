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

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, IntegerValue}

abstract class ArithmeticFunction(name: String) extends CoreFunction(name) {
  protected def toIntegers(arguments: Seq[Expression], environment: Environment): Seq[Int] = arguments.map {
    case IntegerValue(i) => i
    case _ => expected("i1 i2 ...", arguments, environment)
  }
}

object Add extends ArithmeticFunction("+") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    IntegerValue(toIntegers(arguments, environment).sum)
}

object Sub extends ArithmeticFunction("-") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): IntegerValue = arguments match {
    case Seq(IntegerValue(i)) => IntegerValue(-i)
    case Seq(IntegerValue(head), tail @ _*) => IntegerValue(toIntegers(tail, environment).fold(head)(_ - _))
    case _ => expected("i1 in...", arguments, environment)
  }
}

object Mul extends ArithmeticFunction("*") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    IntegerValue(toIntegers(arguments, environment).product)
}

object Div extends ArithmeticFunction("/") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): IntegerValue = {
    val xs = toIntegers(arguments, environment)
    if (xs.length < 2) expected("i1 i2 ...", arguments, environment)
    if (xs.tail.contains(0)) error("Division by zero: " + xs.mkString(" "), environment)
    IntegerValue(xs.reduceLeft(_ / _))
  }
}

object Mod extends ArithmeticFunction("mod") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): IntegerValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) =>
      if (i2 == 0) error("Division by zero", environment) else IntegerValue(i1 % i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}
