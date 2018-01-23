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

import com.pavelfatin.toyide.languages.lisp.core.CoreFunction
import com.pavelfatin.toyide.languages.lisp.value.SymbolValue
import com.pavelfatin.toyide.node._

class SymbolNode extends NodeImpl("symbol")
  with ExpressionNode with ReferenceNode with IdentifiedNode with TargetResolution {

  def read0(source: String) = SymbolValue(text, Some(placeIn(source)))

  def id: Option[NodeImpl] = source

  def source: Option[NodeImpl] = if (quoted) children.drop(1).headOption else children.headOption

  def target: Option[SymbolNode] = if (!resolvable) None else accessibleSymbols.find {
    case SymbolNode(name) => name == identifier
  }

  override def identifier: String = source.map(_.span.text).mkString

  def predefined: Boolean = CoreFunction.Names.contains(identifier)

  def resolvable: Boolean = !(predefined || declaration || title)

  private def declaration: Boolean = parents.exists { parent =>
    localSymbolsIn(parent).contains(this) || globalSymbolsIn(parent).contains(this)
  }

  private def title: Boolean = parent.exists {
    case ListNode(SymbolNode("fn" | "macro"), name: SymbolNode, _*) => name == this
    case _ => false
  }

  override def toString: String = "%s(%s)".format(kind, identifier)
}

object SymbolNode {
  def unapply(node: SymbolNode) = Some(node.identifier)
}
