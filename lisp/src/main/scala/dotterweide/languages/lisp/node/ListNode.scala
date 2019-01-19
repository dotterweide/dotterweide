/*
 *  ListNode.scala
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
import dotterweide.languages.lisp.LispTokens._
import dotterweide.languages.lisp.value.ListValue
import dotterweide.node.NodeImpl

import scala.collection.immutable.{Seq => ISeq}

class ListNode extends NodeImpl("list") with ExpressionNode {
  protected def read0(source: String): ListValue = prefixKind match {
    case Some(HASH) => FunctionLiteral.readFrom(this, source)
    case _ => ListValue(expressions.map(_.read(source)), Some(placeIn(source)))
  }

  def expressions: ISeq[ExpressionNode] = children.filterBy[ExpressionNode]

  def function: Option[ExpressionNode] = expressions.headOption

  def arguments: ISeq[ExpressionNode] = expressions.drop(1)
}

object ListNode {
  def unapplySeq(node: ListNode) = Some(node.expressions)
}
