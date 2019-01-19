/*
 *  VoidValue.scala
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
import dotterweide.languages.toy.ToyType
import dotterweide.languages.toy.ToyType.VoidType
import dotterweide.languages.toy.node._
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object VoidValue extends Inspection {
  val Message = "Value type cannot be void"

  def inspect(node: Node): ISeq[Mark] = node match {
    case p: Parameter if p.nodeType.contains(VoidType) =>
      p.typeId.map(Mark(_, Message)).toList
    case v: VariableDeclaration if v.nodeType.contains(ToyType.VoidType) =>
      v.typeId.map(Mark(_, Message)).toList
    case _ => Nil
  }
}