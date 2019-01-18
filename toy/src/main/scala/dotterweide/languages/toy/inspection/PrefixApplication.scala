/*
 *  PrefixApplication.scala
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
import dotterweide.languages.toy.node.PrefixExpression
import dotterweide.node.{Expression, Node}

object PrefixApplication extends Inspection {
  val Message: (String, String) => String = "Operator '%s' cannot be applied to '%s'".format(_: String, _: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case prefix @ PrefixExpression(Some(token), Some(Expression(expType))) if prefix.nodeType.isEmpty =>
      Mark(prefix, Message(token.span.text, expType.presentation)) :: Nil
    case _ => Nil
  }
}