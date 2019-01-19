/*
 *  IntegerRange.scala
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
import dotterweide.languages.toy.node.Literal
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object IntegerRange extends Inspection {
  val Message = "Integer number too large"

  def inspect(node: Node): ISeq[Mark] = node match {
    case l: Literal if l.nodeType.contains(ToyType.IntegerType) =>
      try {
        Integer.parseInt(l.span.text)
        Nil
      } catch {
        case _: NumberFormatException =>
          Mark(l, Message) :: Nil
      }
    case _ => Nil
  }
}