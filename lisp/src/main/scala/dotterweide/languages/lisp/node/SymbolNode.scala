/*
 *  SymbolNode.scala
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

import dotterweide.languages.lisp.core.CoreFunction
import dotterweide.languages.lisp.value.SymbolValue
import dotterweide.node._
import dotterweide.node.impl.NodeImpl

class SymbolNode extends NodeImpl("symbol")
  with ExpressionNode with ReferenceNode with IdentifiedNode with TargetResolution {

  def read0(source: String) = SymbolValue(text, Some(placeIn(source)))

  def id: Option[NodeImpl] = source

  def source: Option[NodeImpl] = if (quoted) children.drop(1).headOption else children.headOption

  def target: Option[SymbolNode] = if (!resolvable) None else accessibleSymbols.find {
    case SymbolNode(name) => name == identifier
  }

  override def identifier: String = source.map(_.span.text).mkString

  def predefined: Boolean = CoreFunction.Names.contains(identifier)

  def resolvable: Boolean = !(predefined || declaration || title)

  private def declaration: Boolean = parents.exists { parent =>
    localSymbolsIn(parent).contains(this) || globalSymbolsIn(parent).contains(this)
  }

  private def title: Boolean = parent.exists {
    case ListNode(SymbolNode("fn" | "macro"), name: SymbolNode, _*) => name == this
    case _ => false
  }

  override def toString: String = "%s(%s)".format(kind, identifier)
}

object SymbolNode {
  def unapply(node: SymbolNode) = Some(node.identifier)
}
