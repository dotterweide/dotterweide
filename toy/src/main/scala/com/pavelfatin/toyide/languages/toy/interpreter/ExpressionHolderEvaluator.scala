/*
 *  ExpressionHolderEvaluator.scala
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
import com.pavelfatin.toyide.interpreter.{Context, Value}
import com.pavelfatin.toyide.languages.toy.node.ExpressionHolder
import com.pavelfatin.toyide.node.Node

trait ExpressionHolderEvaluator extends ToyEvaluable { self: ExpressionHolder with Node =>
  protected def evaluateExpression(context: Context, output: Output): Value = {
    val exp = expression.getOrElse(
      interrupt(context, "Expression not found: %s", span.text))

    val value = exp.evaluate(context, output).getOrElse(
      interrupt(context, "Expression return no value: %s", exp.span.text))

    expectedType.foreach { expected =>
      val actual = value.valueType
      if (actual != expected) {
        interrupt(context, "Type mismatch, expected: %s, actual: %s", expected.presentation, actual.presentation)
      }
    }

    value
  }
}