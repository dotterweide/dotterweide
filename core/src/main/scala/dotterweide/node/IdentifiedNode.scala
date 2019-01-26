/*
 *  IdentifiedNode.scala
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

trait IdentifiedNode extends NodeWithIdentifier {
  // ---- abstract ----

  def id: Option[Node]

  // ---- impl ----

  def identifier: String = id.map(_.span.text).mkString

  override def toString: String = "%s(%s)".format(kind, identifier)
}

object IdentifiedNode {
  def unapply(node: IdentifiedNode): Option[(Node, String)] =
    node.id.map((_, node.identifier))
}

object IdentifiedNodeId {
  def unapply(node: IdentifiedNode): Option[Node] =
    node.id
}