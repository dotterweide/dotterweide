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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters

class MacroFunction(val name: Option[String], parameters: Parameters, expressions: Seq[Expression], closure: Map[String, Expression]) extends FunctionValue {
  def isLazy = true

  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = {
    val expansion = expand(arguments, environment, output)
    expansion.eval(environment, output)
  }

  def expand(arguments: Seq[Expression], environment: Environment, output: Output): Expression = {
    val bindings = parameters.bind(ListValue(arguments)).fold(error(_, environment), identity)

    val env = environment.addLocals(closure ++ bindings)

    expressions.map(_.eval(env, output)).lastOption.getOrElse(ListValue.Empty)
  }

  def presentation: String = {
    val prefix = name.fold("")(_ + "_")
    prefix + "macro" + parameters.presentation
  }
}
