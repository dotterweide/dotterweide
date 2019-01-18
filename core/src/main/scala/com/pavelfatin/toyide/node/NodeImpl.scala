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

package com.pavelfatin.toyide.node

import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.Span

class NodeImpl(val kind: String) extends Node {
  var token: Option[Token] = None

  var span: Span = Span("", 0, 0)

  var problem: Option[String] = None

  var parent: Option[Node] = None

  var previousSibling: Option[Node] = None

  var nextSibling: Option[Node] = None

  private var _children: Seq[NodeImpl] = Nil

  def children: Seq[NodeImpl] = _children

  def children_=(children: Seq[NodeImpl]): Unit = {
    val first = children.head.span
    span = Span(first.source, first.begin, children.last.span.end)
    _children = children
    children.foreach(_.parent = Some(this))
    for((a, b) <- children.zip(children.tail)) {
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