/*
 *  AssignmentEvaluator.scala
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
import dotterweide.languages.toy.node._

trait AssignmentEvaluator extends ExpressionHolderEvaluator { self: Assignment =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val ref = reference.getOrElse(
      interrupt(context, "Reference for assignment not found %s", span.text))

    val target = ref.target.getOrElse(
      interrupt(context, "Target for reference not found %s", ref.span.text))

    def local = target match {
      case v: VariableDeclaration => v.local
      case _: Parameter           => true
      case _ => interrupt(context, "Non-value target for reference %s: %s", ref.identifier, target.span.text)
    }

    val newValue = evaluateExpression(context, output)

    wrap(context) {
      context.update(local, ref.identifier, newValue)
    }

    None
  }
}