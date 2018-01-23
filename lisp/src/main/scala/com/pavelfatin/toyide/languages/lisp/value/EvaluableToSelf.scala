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
import com.pavelfatin.toyide.interpreter.DelegateValue
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.node.NodeType

trait EvaluableToSelf extends Expression {
  def eval(environment: Environment, output: Output): Expression = this
}

case class BooleanValue(content: Boolean) extends DelegateValue[Boolean] with EvaluableToSelf {
  def valueType: NodeType = LispType.BooleanType
}

case class IntegerValue(content: Int) extends DelegateValue[Int] with EvaluableToSelf {
  def valueType: NodeType = LispType.IntegerType
}

case class CharacterValue(content: Char) extends DelegateValue[Char] with EvaluableToSelf {
  def valueType: NodeType = LispType.CharacterType

  override def presentation: String = {
    val s = content match {
      case ' ' => "space"
      case '\t' => "tab"
      case '\r' => "return"
      case '\n' => "newline"
      case c => c
    }
    "\\" + s
  }
}
