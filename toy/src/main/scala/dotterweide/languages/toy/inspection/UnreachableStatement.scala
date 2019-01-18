/*
 *  UnreachableStatement.scala
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
import dotterweide.languages.toy.node._
import dotterweide.node.Node

object UnreachableStatement extends Inspection {
  val Message = "Unreachable statement"

  def inspect(node: Node): Seq[Mark] = node match {
    case FunctionBlock(ScopeExit(exit)) =>
      exit.nextSiblings.filterNot(_.isInstanceOf[Comment]).find(!_.isLeaf) match {
        case Some(sibling) => Mark(sibling, Message) :: Nil
        case _ => Nil
      }
    case _ => Nil
  }
}