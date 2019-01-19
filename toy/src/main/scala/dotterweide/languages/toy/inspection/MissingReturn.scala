/*
 *  MissingReturn.scala
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

package dotterweide.languages.toy.inspection

import dotterweide.inspection.{Inspection, Mark}
import dotterweide.languages.toy.ToyType.VoidType
import dotterweide.languages.toy.node._
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object MissingReturn extends Inspection {
  val Message = "Missing return statement"

  def inspect(node: Node): ISeq[Mark] = node match {
    case function @ FunctionBlock(block) if !function.nodeType.contains(VoidType) && block.exit.isEmpty =>
      Mark(block.children.last, Message) :: Nil
    case _ => Nil
  }
}