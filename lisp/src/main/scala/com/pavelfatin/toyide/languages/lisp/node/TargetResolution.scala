/*
 *  TargetResolution.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

/*
 * Original code copyright 2018 Pavel Fatin, https://pavelfatin.com
 * Licensed under the Apache License, Version 2.0 (the "License"): http://www.apache.org/licenses/LICENSE-2.0
 */

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.node.Node

trait TargetResolution { self: ExpressionNode =>
  def accessibleSymbols: Seq[SymbolNode] =
    parents.flatMap(localSymbolsIn) ++ parents.last.children.flatMap(globalSymbolsIn)

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
