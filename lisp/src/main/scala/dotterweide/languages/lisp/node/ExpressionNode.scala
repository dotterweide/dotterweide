/*
 *  ExpressionNode.scala
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

package dotterweide.languages.lisp.node

import dotterweide.Extensions._
import dotterweide.interpreter.Place
import dotterweide.node.Node

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