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
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, CharacterValue, Environment, Expression, IntegerValue, ListValue}

object Gt extends CoreFunction(">") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 > i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object GtEq extends CoreFunction(">=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 >= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Lt extends CoreFunction("<") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 < i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object LtEq extends CoreFunction("<=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 <= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Eq extends CoreFunction("=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    BooleanValue(apply0(arguments, environment, output))

  private def apply0(arguments: Seq[Expression], environment: Environment, output: Output): Boolean = {
    arguments match {
      case Seq(IntegerValue(i1), IntegerValue(i2)) => i1 == i2
      case Seq(CharacterValue(c1), CharacterValue(c2)) => c1 == c2
      case Seq(BooleanValue(b1), BooleanValue(b2)) => b1 == b2
      case Seq(ListValue(l1), ListValue(l2)) =>
        l1.size == l2.size && l1.zip(l2).map(p => Seq(p._1, p._2)).forall(apply0(_, environment, output))
      case _ => expected("v1 v2", arguments, environment)
    }
  }
}
