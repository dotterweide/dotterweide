/*
 *  PrefixExpressionEvaluator.scala
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
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue._
import com.pavelfatin.toyide.languages.toy.node.PrefixExpression

trait PrefixExpressionEvaluator extends ToyEvaluable { self: PrefixExpression =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val t = prefix.getOrElse(
      interrupt(context, "Prefix token not found: %s", span.text))

    val exp = expression.getOrElse(
      interrupt(context, "Inner expression not found: %s", span.text))

    val value = exp.evaluate(context, output)

    t.kind match {
      case BANG => value match {
        case Some(BooleanValue(v)) => Some(BooleanValue(!v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case PLUS => value match {
        case Some(IntegerValue(v)) => Some(IntegerValue(v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case MINUS => value match {
        case Some(IntegerValue(v)) => Some(IntegerValue(-v))
        case _ => interrupt(context, "Wrong inner expression type: %s", exp.span.text)
      }
      case _ => interrupt(context, "Incorrect prefix: %s", t.span.text)
    }
  }
}