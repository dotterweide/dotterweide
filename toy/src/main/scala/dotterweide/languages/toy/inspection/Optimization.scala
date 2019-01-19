/*
 *  Optimization.scala
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

import dotterweide.inspection.{Decoration, Inspection, Mark}
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object Optimization extends Inspection {
  val Message: String => String = "Can be simplified to '%s' (use Code / Optimize)".format(_: String)

  def inspect(node: Node): ISeq[Mark] = {
    node.parent match {
      case Some(p) if p.optimized.isDefined => Nil
      case _ => node.optimized match {
        case Some(s) if s != node.span.text => Mark(node, Message(s), Decoration.Fill, warning = true) :: Nil
        case _ => Nil
      }
    }
  }
}