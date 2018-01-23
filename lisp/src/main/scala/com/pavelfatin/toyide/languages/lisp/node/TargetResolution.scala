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

import com.pavelfatin.toyide.node.Node

trait TargetResolution { self: ExpressionNode =>
  def accessibleSymbols: Seq[SymbolNode] =
    parents.flatMap(localSymbolsIn).toSeq ++ parents.last.children.flatMap(globalSymbolsIn).toSeq

  protected def localSymbolsIn(node: Node): Set[SymbolNode] = node match {
    case ListNode(SymbolNode("fn" | "macro"), ListNode(ps @ _*), _*) => symbolsInPatterns(ps)
    case ListNode(SymbolNode("loop" | "let" | "if-let"), ListNode(bs @ _*), _*) => symbolsInBindings(bs)
    case ListNode(SymbolNode("fn" | "macro" | "defn" | "defmacro"), SymbolNode(_), ListNode(ps @ _*), _*) => symbolsInPatterns(ps)
    case _ => Set.empty
  }

  protected def globalSymbolsIn(node: Node): Set[SymbolNode] = node match {
    case ListNode(SymbolNode("def" | "defn" | "defmacro"), symbol: SymbolNode, _*) => Set(symbol)
    case _ => Set.empty
  }

  private def symbolsInPatterns(patterns: Seq[ExpressionNode]): Set[SymbolNode] = {
    val symbols = patterns.flatMap(_.elements).collect {
      case symbol: SymbolNode => symbol
    }
    symbols.toSet
  }

  private def symbolsInBindings(bindings: Seq[ExpressionNode]): Set[SymbolNode] = {
    val patterns = bindings.grouped(2).map(_.head).toSeq
    symbolsInPatterns(patterns)
  }
}
