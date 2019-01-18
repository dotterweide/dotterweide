/*
 *  Node.scala
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

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Span
import com.pavelfatin.toyide.compiler.Translatable
import com.pavelfatin.toyide.interpreter.Evaluable
import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.optimizer.Optimizable

trait Node extends Evaluable with Translatable with Optimizable {
  /** Used in `toString` representation. Does not have any other use. */
  def kind: String

  def token: Option[Token]

  def span: Span

  def problem: Option[String]

  def children: Seq[Node]

  def parent: Option[Node]

  def previousSibling: Option[Node]

  def nextSibling: Option[Node]

  def parents: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.parent match {
        case Some(parent) => parent #:: of(parent)
        case None => Stream.empty
      }
    }
    of(this)
  }

//  def root: Node = parents.lastOption.getOrElse(this)

  def previousSiblings: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.previousSibling match {
        case Some(sibling) => sibling #:: of(sibling)
        case None => Stream.empty
      }
    }
    of(this)
  }

  def nextSiblings: Seq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.nextSibling match {
        case Some(sibling) => sibling #:: of(sibling)
        case None => Stream.empty
      }
    }
    of(this)
  }

  def isLeaf: Boolean = token.isDefined

  def elements: Seq[Node] = {
    def elements(node: Node): Stream[Node] =
      node #:: node.children.toStream.flatMap(elements)
    elements(this)
  }

  def leafAt(offset: Int): Option[Node] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    elements.filter(_.span.touches(span.begin + offset)).find(_.isLeaf)
  }

  def referenceAt(offset: Int): Option[ReferenceNode] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    elements.filter(_.span.touches(span.begin + offset)).findBy[ReferenceNode]
  }

  def identifierAt(offset: Int): Option[IdentifiedNode] = {
    if (offset < 0 || offset > span.end)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.begin, span.end))
    val candidates = elements.filter(_.span.touches(span.begin + offset)).collect {
      case node @ NodeParent(identified: IdentifiedNode) if identified.id.contains(node) => identified
    }
    candidates.headOption
  }

  def content: String = {
    def indent(s: String, level: Int) =
      s.split("\n").map(Array.fill(level)("  ").mkString + _).mkString("\n")

    val prefix = if (problem.isDefined) "error: " else ""

    if (isLeaf)
      prefix + token.get.toString
    else
      prefix + kind + "\n" + children.map(n => indent(n.content, 1)).mkString("\n")
  }

  override def toString: String = {
    val prefix = if (problem.isDefined) "error: " else ""

    if (isLeaf)
      prefix + token.get.toString
    else
      prefix + kind
  }
}

object NodeToken {
  def unapply(node: Node): Option[Token] = node.token
}

object NodeParent {
  def unapply(node: Node): Option[Node] = node.parent
}

object NodeNextSibling {
  def unapply(node: Node): Option[Node] = node.nextSibling
}
