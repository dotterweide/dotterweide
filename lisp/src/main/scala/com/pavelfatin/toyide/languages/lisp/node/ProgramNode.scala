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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.compiler.{Labels, TranslationException}
import com.pavelfatin.toyide.interpreter.Context
import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, ListValue}
import com.pavelfatin.toyide.node.NodeImpl

class ProgramNode extends NodeImpl("program") {
  def expressions: Seq[ExpressionNode] = children.filterBy[ExpressionNode]

  override def evaluate(context: Context, output: Output): Option[Expression] = {
    val environment = Library.instance.createEnvironment()
    val value = evaluate(ProgramNode.Source, environment, output)
    Some(value)
  }

  def evaluate(source: String, environment: Environment, output: Output): Expression = {
    val values = expressions.map(_.read(source))
    values.foldLeft[Expression](ListValue.Empty)((_, x) => x.eval(environment, output))
  }

  override def translate(className: String, labels: Labels) =
    throw TranslationException("Translation to bytecode is not yet implemented.")
}

object ProgramNode {
  private val Source = "User"
}