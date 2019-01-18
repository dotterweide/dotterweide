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

package com.pavelfatin.toyide.node

trait ReferenceNode extends Node {
  def source: Option[Node]

  def identifier: String = source.map(_.span.text).mkString

  def target: Option[Node]

  def isReferenceTo(node: Node): Boolean = target match {
    case Some(it) => it == node
    case None     => false
  }

  def predefined: Boolean

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