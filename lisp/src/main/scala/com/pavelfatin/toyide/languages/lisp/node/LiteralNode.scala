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

import com.pavelfatin.toyide.languages.lisp.core.NewList
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, CharacterValue, Expression, IntegerValue}
import com.pavelfatin.toyide.node.NodeImpl

trait LiteralNode extends ExpressionNode {
  override def toString: String = "%s(%s)".format(kind, span.text)
}

class IntegerLiteralNode extends NodeImpl("integer") with LiteralNode {
  def read0(source: String) = IntegerValue(text.toInt)
}

class BooleanLiteralNode extends NodeImpl("boolean") with LiteralNode {
  def read0(source: String) = BooleanValue(text.toBoolean)
}

class CharacterLiteralNode extends NodeImpl("character") with LiteralNode {
  def read0(source: String): CharacterValue = {
    val c = text.substring(1) match {
      case "return" => '\r'
      case "newline" => '\n'
      case "tab" => '\t'
      case "space" => ' '
      case s => s.charAt(0)
    }
    CharacterValue(c)
  }
}

class StringLiteralNode extends NodeImpl("string") with LiteralNode {
  def read0(source: String): Expression = {
    val s = text
    val characters = s.substring(1, s.length - 1)
      .replace("\\r", "\r")
      .replace("\\n", "\n")
      .replace("\\t", "\t")
      .replace("\\\\", "\\")
      .toSeq
    NewList(characters.map(CharacterValue))
  }
}
