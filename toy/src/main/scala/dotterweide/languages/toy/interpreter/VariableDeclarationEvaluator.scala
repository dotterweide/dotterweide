/*
 *  VariableDeclarationEvaluator.scala
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

package dotterweide.languages.toy.interpreter

import dotterweide.Output
import dotterweide.interpreter.{Context, Value}
import dotterweide.languages.toy.node.VariableDeclaration

trait VariableDeclarationEvaluator extends ExpressionHolderEvaluator { self: VariableDeclaration =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val value = evaluateExpression(context, output)
    wrap(context) {
      context.put(local, identifier, value)
    }
    None
  }
}