/*
 *  ReferenceNode.scala
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

trait ReferenceNode extends NodeWithIdentifier {
  // ---- abstract ----

  /** The local identifier */
  def source: Option[Node]

  /** The actual reference location */
  def target: Option[Node]

  def predefined: Boolean

  // ---- impl ----

  def identifier: String = source.map(_.span.text).mkString

  def isReferenceTo(node: Node): Boolean = target.contains(node)

  def unresolved: Boolean = !predefined && target.isEmpty

  override def toString: String = "%s(%s)".format(kind, identifier)
}

object ReferenceNode {
  def unapply(reference: ReferenceNode): Option[(Option[Node], Option[Node])] =
    Some((reference.source, reference.target))
}

object ReferenceNodeTarget {
  def unapply(reference: ReferenceNode): Option[Node] = reference.target
}