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
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, FunctionValue, ListValue, SymbolValue, UserFunction}

object Fn extends CoreFunction("fn", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): UserFunction = {
    def createFunction(name: Option[String], parameterList: ListValue, expressions: Seq[Expression]) = {
      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)
      new UserFunction(name, parameters, expressions, environment.locals)
    }

    arguments match {
      case Seq(parameterList: ListValue, expressions @ _*) =>
        createFunction(None, parameterList, expressions)
      case Seq(SymbolValue(name), parameterList: ListValue, expressions @ _*) =>
        createFunction(Some(name), parameterList, expressions)
      case _ => expected("name? [params*] exprs*", arguments, environment)
    }
  }
}

object Apply extends CoreFunction("apply") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(f: FunctionValue, ListValue(expressions)) =>
      f.apply(expressions, environment, output)
    case _ => expected("f args", arguments, environment)
  }
}
