/*
 *  IfEvaluator.scala
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
import dotterweide.languages.toy.node.If

trait IfEvaluator extends ConditionHolderEvaluator { self: If =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    if (evaluateCondition(context, output)) {
      val scope = block.getOrElse(
        interrupt(context, "If block not found %s", span.text))
      scope.evaluate(context, output)
    } else {
      elseBlock.foreach {
        _.evaluate(context, output)
      }
    }
    None
  }
}