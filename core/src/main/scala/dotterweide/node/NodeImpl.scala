/*
 *  NodeImpl.scala
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

package dotterweide.node

import dotterweide.Span
import dotterweide.lexer.Token

import scala.collection.immutable.{Seq => ISeq}

class NodeImpl(val kind: String) extends Node {
  var token: Option[Token] = None

  var span: Span = Span("", 0, 0)

  var problem: Option[String] = None

  var parent: Option[Node] = None

  var previousSibling: Option[Node] = None

  var nextSibling: Option[Node] = None

  private var _children: ISeq[NodeImpl] = Nil

  def children: ISeq[NodeImpl] = _children

  override def elements: ISeq[NodeImpl] = {
    def loop(node: NodeImpl): Stream[NodeImpl] =
      node #:: node.children.toStream.flatMap(loop)
    loop(this)
  }

  /** Sets the child nodes. As a side effect,
    * updates `span`, and for the children, updates their
    * `nextSibling`, `previousSibling`, and `parent` fields.
    */
  def children_=(children: ISeq[NodeImpl]): Unit = if (children.nonEmpty) {
    val first   = children.head.span
    _children   = children
    val par     = Some(this)
    var cBegin  = first.start
    var cEnd    = first.stop
    children.foreach { child =>
      child.parent  = par
      val cSpan     = child.span
      if (cSpan.start < cBegin) cBegin = cSpan.start
      if (cSpan.stop   > cEnd  ) cEnd   = cSpan.stop
    }
    span = Span(first.source, cBegin, cEnd)
    for ((a, b) <- children.zip(children.tail)) {
      a.nextSibling     = Some(b)
      b.previousSibling = Some(a)
    }
  }
}

object NodeImpl {
  def createLeaf(token: Token): NodeImpl = {
    val node      = new NodeImpl("leaf")
    node.token    = Some(token)
    node.span     = token.span
    node
  }

  def createError(token: Option[Token], span: Span, message: String): NodeImpl = {
    val node      = new NodeImpl("leaf")
    node.span     = span
    node.token    = token
    node.problem  = Some(message)
    node
  }
}