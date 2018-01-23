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
import com.pavelfatin.toyide.interpreter.Place
import com.pavelfatin.toyide.node.Node

trait ExpressionNode extends Node with ReadableNode with QuotableNode {
  def placeIn(source: String): Place = {
    val line = span.source.take(span.begin).count(_ == '\n')
    Place(Some(enclosure.map(source + "." + _).getOrElse(source)), line)
  }

  private def enclosure: Option[String] = {
    val enclosures = parents.flatMap {
      case list: ListNode => list.expressions match {
        case Seq(SymbolNode("fn" | "macro" | "defn" | "defmacro"), SymbolNode(name), _ *) => name :: Nil
        case _ => Nil
      }
      case _ => Nil
    }
    enclosures.headOption
  }
}