/*
 *  BinaryExpressionEvaluator.scala
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
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.languages.toy.interpreter.ToyValue.{BooleanValue, _}
import com.pavelfatin.toyide.languages.toy.node.BinaryExpression
import com.pavelfatin.toyide.lexer.TokenKind

trait BinaryExpressionEvaluator extends ToyEvaluable { self: BinaryExpression =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val (leftNode, token, rightNode) = parts.getOrElse(
      interrupt(context, "Incorrect expression: %s", span.text))

    val leftType = leftNode.nodeType.getOrElse(
      interrupt(context, "Unknow left operand type: %s", span.text))

    val rightType = rightNode.nodeType.getOrElse(
      interrupt(context, "Unknow right operand type: %s", span.text))

    lazy val leftValue = leftNode.evaluate(context, output).getOrElse(
      interrupt(context, "Left expression return no value: %s", span.text))

    lazy val rightValue = rightNode.evaluate(context, output).getOrElse(
      interrupt(context, "Right expression return no value: %s", span.text))

    val value = (leftType, token.kind, rightType) match {
      case (BooleanType, AMP_AMP, BooleanType) =>
        BooleanValue(leftValue.asInstanceOf[BooleanValue].content && rightValue.asInstanceOf[BooleanValue].content)
      case (BooleanType, BAR_BAR, BooleanType) =>
        BooleanValue(leftValue.asInstanceOf[BooleanValue].content || rightValue.asInstanceOf[BooleanValue].content)
      case _ => evaluateValues(context, leftValue, token.kind, rightValue)
    }

    Some(value)
  }

  private def evaluateValues(context: Context, leftValue: Value, kind: TokenKind, rightValue: Value) = {
    (leftValue, kind, rightValue) match {
      case (IntegerValue(l), GT   , IntegerValue(r)) => BooleanValue(l > r)
      case (IntegerValue(l), GT_EQ, IntegerValue(r)) => BooleanValue(l >= r)
      case (IntegerValue(l), LT   , IntegerValue(r)) => BooleanValue(l < r)
      case (IntegerValue(l), LT_EQ, IntegerValue(r)) => BooleanValue(l <= r)

      case (StringValue (l), EQ_EQ, StringValue (r)) => BooleanValue(l == r)
      case (IntegerValue(l), EQ_EQ, IntegerValue(r)) => BooleanValue(l == r)
      case (BooleanValue(l), EQ_EQ, BooleanValue(r)) => BooleanValue(l == r)

      case (StringValue (l), BANG_EQ, StringValue (r)) => BooleanValue(l != r)
      case (IntegerValue(l), BANG_EQ, IntegerValue(r)) => BooleanValue(l != r)
      case (BooleanValue(l), BANG_EQ, BooleanValue(r)) => BooleanValue(l != r)

      case (IntegerValue(l), STAR, IntegerValue(r)) => IntegerValue(l * r)
      case (IntegerValue(l), SLASH, IntegerValue(r)) =>
        if (r == 0) interrupt(context, "Division by zero") else IntegerValue(l / r)
      case (IntegerValue(l), PERCENT, IntegerValue(r)) =>
        if (r == 0) interrupt(context, "Division by zero") else IntegerValue(l % r)

      case (IntegerValue(l), PLUS , IntegerValue(r))  => IntegerValue(l + r)
      case (IntegerValue(l), MINUS, IntegerValue(r))  => IntegerValue(l - r)
      case (StringValue (l), PLUS, r)                 => StringValue(l + r.presentation)

      case _ => interrupt(context, "Incorrect expression: %s", span.text)
    }
  }
}