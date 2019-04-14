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

package dotterweide.node

import dotterweide.Extensions._
import dotterweide.Span
import dotterweide.compiler.Translatable
import dotterweide.interpreter.Evaluable
import dotterweide.lexer.Token
import dotterweide.optimizer.Optimizable

import scala.collection.immutable.{Seq => ISeq}

trait Node extends Evaluable with Translatable with Optimizable {
  /** Used in `toString` representation. Does not have any other use. */
  def kind: String

  def token: Option[Token]

  def span: Span

  def problem: Option[String]

  def children: ISeq[Node]

  def parent: Option[Node]

  def previousSibling : Option[Node]
  def nextSibling     : Option[Node]

  def parents: ISeq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.parent match {
        case Some(parent)   => parent #:: of(parent)
        case None           => Stream.empty
      }
    }
    of(this)
  }

  def previousSiblings: ISeq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.previousSibling match {
        case Some(sibling)  => sibling #:: of(sibling)
        case None           => Stream.empty
      }
    }
    of(this)
  }

  def nextSiblings: ISeq[Node] = {
    def of(node: Node): Stream[Node] = {
      node.nextSibling match {
        case Some(sibling)  => sibling #:: of(sibling)
        case None           => Stream.empty
      }
    }
    of(this)
  }

  def isLeaf: Boolean = token.isDefined

  /** Total sequence of nodes (flattened) */
  def elements: ISeq[Node] = {
    def loop(node: Node): Stream[Node] =
      node #:: node.children.toStream.flatMap(loop)
    loop(this)
  }

  def leafAt(offset: Int): Option[Node] = {
    if (offset < 0 || offset > span.stop)
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.start, span.stop))
    val offAbs = span.start + offset
    elements.filter(_.span.touchesNonEmpty(offAbs)).find(_.isLeaf)
  }

  def referenceAt(offset: Int): Option[ReferenceNode] = {
    if (offset < 0 || offset > span.stop) {
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.start, span.stop))
    }
    val flt = elements.filter(_.span.touchesNonEmpty(span.start + offset))
    flt.findBy[ReferenceNode]
  }

  def identifierAt(offset: Int): Option[IdentifiedNode] = {
    if (offset < 0 || offset > span.stop) {
      throw new IllegalArgumentException("Offset (%d) must be in (%d; %d)".format(offset, span.start, span.stop))
    }
    val candidates = elements.filter(_.span.touchesNonEmpty(span.start + offset)).collect {
      case node @ NodeParent(identified: IdentifiedNode) if identified.id.contains(node) => identified
    }
    candidates.headOption
  }

  def content: String = {
    def indent(s: String, level: Int) =
      s.split("\n").map(Array.fill(level)("  ").mkString + _).mkString("\n")

    val prefix = if (problem.isDefined) "error: " else ""

    val suffix = token match {
      case Some(t)  => t.toString
      case None     => kind + "\n" + children.map(n => indent(n.content, 1)).mkString("\n")
    }

    prefix + suffix
  }

  override def toString: String = {
    val prefix = if (problem.isDefined) "error: " else ""

    val suffix = token match {
      case Some(t)  => t.toString
      case None     => kind
    }

    prefix + suffix
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
