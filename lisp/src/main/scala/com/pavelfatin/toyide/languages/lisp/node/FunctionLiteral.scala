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

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue, SymbolValue}

object FunctionLiteral {
  private val PositionalArgument = "%(\\d+)".r

  def readFrom(list: ListNode, source: String): ListValue = {
    val nodes = list.expressions

    val parameters = parametersFor(indicesIn(nodes))

    val expressions = expressionsFor(nodes, source)

    ListValue(Seq(SymbolValue("fn"), ListValue(parameters), ListValue(expressions, Some(list.placeIn(source)))))
  }

  private def indicesIn(nodes: Seq[ExpressionNode]): Seq[Int] = nodes.flatMap {
    case SymbolNode(symbol)       => indexFor(symbol).toSeq
    case ListNode(children @ _*)  => indicesIn(children)
    case _                        => Nil
  }

  private def indexFor(symbol: String): Option[Int] = Some(symbol) collect {
    case "%" => 1
    case "%&" => -1
    case PositionalArgument(digits) => digits.toInt
  }

  private def parametersFor(indices: Seq[Int]): Seq[Expression] = {
    if (indices.isEmpty) Nil else
      Range(1, indices.max + 1).map(parameterFor) ++
        (if (indices.contains(-1)) Seq(SymbolValue("&"), parameterFor(-1)) else Nil)
  }

  private def parameterFor(index: Int): Expression = {
    val name = if (index == -1) "_ps" else "_p" + index
    SymbolValue(name)
  }

  private def expressionsFor(nodes: Seq[ExpressionNode], source: String): Seq[Expression] = nodes.map {
    case node @ SymbolNode(symbol) =>
      indexFor(symbol).map(parameterFor).getOrElse(node.read(source))
    case list @ ListNode(children @ _*) =>
      ListValue(expressionsFor(children, source), Some(list.placeIn(source)))
    case node => node.read(source)
  }
}

