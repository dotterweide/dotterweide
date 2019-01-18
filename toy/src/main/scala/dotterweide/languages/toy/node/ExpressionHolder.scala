/*
 *  ExpressionHolder.scala
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

package dotterweide.languages.toy.node

import dotterweide.node._

trait ExpressionHolder { self: Node =>
  def expression: Option[Expression]

  def expectedType: Option[NodeType]
}

object ExpressionHolder {
  def unapply(holder: ExpressionHolder) = Some((holder.expression, holder.expectedType))
}
