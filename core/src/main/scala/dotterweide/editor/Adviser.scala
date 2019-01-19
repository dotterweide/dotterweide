/*
 *  Adviser.scala
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

package dotterweide.editor

import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

trait Adviser {
  def variants(root: Node, anchor: Node): ISeq[Variant]
}

object Adviser {
  /** An intermediate label inserted into the text to mark completion point. */
  val Anchor = "ANCHOR" // XXX TODO --- surely this does no work for every parser?
}

case class Variant(title: String, content: String, shift: Int) {
  override def toString: String = title
}
