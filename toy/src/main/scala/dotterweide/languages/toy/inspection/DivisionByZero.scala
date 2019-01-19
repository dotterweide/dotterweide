/*
 *  DivisionByZero.scala
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
import dotterweide.languages.toy.ToyTokens._
import dotterweide.languages.toy.ToyType._
import dotterweide.languages.toy.node.BinaryExpression
import dotterweide.lexer.Token
import dotterweide.node.{Expression, Node, NodeToken}

import scala.collection.immutable.{Seq => ISeq}

object DivisionByZero extends Inspection {
  val Message = "Division by zero"

  def inspect(node: Node): ISeq[Mark] = node match {
    case e: BinaryExpression => e.children match {
        case Expression(IntegerType) :: NodeToken(Token(SLASH, _, _)) ::  (r @ Expression(IntegerType)) :: Nil
          if r.optimized.contains("0") => Mark(e, Message, Decoration.Fill, warning = true) :: Nil
        case _ => Nil
      }
    case _ => Nil
  }
}