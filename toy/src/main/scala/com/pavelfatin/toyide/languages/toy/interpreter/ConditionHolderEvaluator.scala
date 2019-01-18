/*
 *  ConditionHolderEvaluator.scala
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

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{Context, ValueType}
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue._
import com.pavelfatin.toyide.languages.toy.node.ConditionHolder
import com.pavelfatin.toyide.node.Node

trait ConditionHolderEvaluator extends ExpressionHolderEvaluator { self: ConditionHolder with Node =>
  protected def evaluateCondition(context: Context, output: Output): Boolean =
    evaluateExpression(context, output) match {
      case BooleanValue(b) => b
      case ValueType(t) => interrupt(context, "Wrong type (%s) for condition: %s", t.presentation, span.text)
    }
}