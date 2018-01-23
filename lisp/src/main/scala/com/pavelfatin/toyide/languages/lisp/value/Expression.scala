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
import com.pavelfatin.toyide.interpreter.Value

trait Expression extends Value {
  def eval(environment: Environment, output: Output): Expression

  override def toString: String = "%s: %s".format(presentation, valueType.presentation)
}

object Expression {
  def format(expressions: Seq[Expression]): String =
    expressions.map(format).mkString(" ")

  private def format(expression: Expression): String = expression match {
    case StringValue(s) => s
    case value => value.presentation
  }
}