/*
 *  OperatorApplication.scala
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
import dotterweide.languages.toy.node.BinaryExpression
import dotterweide.node.{Expression, Node}

object OperatorApplication extends Inspection {
  val Message: (String, String, String) => String =
    "Operator '%s' cannot be applied to '%s', '%s'".format(_: String, _: String, _: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case exp @ BinaryExpression(Expression(leftType), token, Expression(rightType)) if exp.nodeType.isEmpty =>
      Mark(exp, Message(token.span.text, leftType.presentation, rightType.presentation)) :: Nil
    case _ => Nil
  }
}