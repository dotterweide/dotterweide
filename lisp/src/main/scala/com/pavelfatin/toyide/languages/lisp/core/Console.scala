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
import com.pavelfatin.toyide.languages.lisp.value.{CharacterValue, Environment, Expression, ListValue}

object Print extends CoreFunction("print") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    output.print(Expression.format(arguments))
    ListValue.Empty
  }
}

object PrintLn extends CoreFunction("println") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    output.print(Expression.format(arguments) + "\n")
    ListValue.Empty
  }
}

object Trace extends CoreFunction("trace") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(v) => output.print(v.presentation); v
    case _ => expected("value", arguments, environment)
  }
}

object Format extends CoreFunction("format") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    ListValue(Expression.format(arguments).map(CharacterValue))
  }
}
