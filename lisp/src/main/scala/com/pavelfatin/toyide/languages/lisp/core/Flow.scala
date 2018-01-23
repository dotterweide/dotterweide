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
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, Environment, Expression, ListValue, RecurValue, TailCalls}

object Do extends CoreFunction("do") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression =
    arguments.lastOption.getOrElse(ListValue.Empty)
}

object If extends CoreFunction("if", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = {
    arguments match {
      case Seq(condition, left) =>
        if (isTruthy(condition.eval(environment, output))) left.eval(environment, output)
        else ListValue.Empty
      case Seq(condition, left, right) =>
        if (isTruthy(condition.eval(environment, output))) left.eval(environment, output)
        else right.eval(environment, output)
      case _ => expected("b exp1 [exp2]", arguments, environment)
    }
  }

  private def isTruthy(expression: Expression) = expression match {
    case BooleanValue(b) => b
    case ListValue(l) => l.nonEmpty
    case _ => true
  }
}

object Error extends CoreFunction("error") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression =
    environment.interrupt(Expression.format(arguments))
}

object Loop extends CoreFunction("loop", isLazy = true) with Bindings with TailCalls {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(ListValue(elements), expressions @ _*) =>
      val parameterList = ListValue(elements.grouped(2).toSeq.map(_.head))

      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)

      withTailCalls(parameters, bind(elements, environment, output)) { env =>
        expressions.map(_.eval(env, output)).lastOption.getOrElse(ListValue.Empty)
      }
    case _ => expected("[bindings*] exprs*", arguments, environment)
  }
}

object Recur extends CoreFunction("recur") with Bindings {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    RecurValue(arguments)
}