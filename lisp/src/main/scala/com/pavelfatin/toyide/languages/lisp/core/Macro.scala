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
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, ListValue, MacroFunction, SymbolValue}

object Eval extends CoreFunction("eval") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(expression) => expression.eval(environment, output)
    case _ => expected("expression", arguments, environment)
  }
}

object Macro extends CoreFunction("macro", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): MacroFunction = {
    def createMacro(name: Option[String], parameterList: ListValue, expressions: Seq[Expression]) = {
      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)
      new MacroFunction(name, parameters, expressions, environment.locals)
    }

    arguments match {
      case Seq(parameterList: ListValue, expressions @ _*) =>
        createMacro(None, parameterList, expressions)
      case Seq(SymbolValue(name), parameterList: ListValue, expressions @ _*) =>
        createMacro(Some(name), parameterList, expressions)
      case _ => expected("[params*] exprs*", arguments, environment)
    }
  }
}

object Macroexpand extends CoreFunction("macroexpand") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(ListValue(Seq(head, tail @ _*))) => head.eval(environment, output) match {
      case m: MacroFunction => m.expand(tail, environment, output)
      case _ => error("macro application expected", environment)
    }
    case _ => expected("list", arguments, environment)
  }
}
