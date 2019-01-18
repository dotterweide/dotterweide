/*
 *  LiteralEvaluator.scala
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
import com.pavelfatin.toyide.languages.toy.node.Literal

trait LiteralEvaluator extends ToyEvaluable { self: Literal =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val s = span.text

    val kind = tokenKind.getOrElse(
      interrupt(context, "Unable to determine token kind for literal", span.text))

    val value = kind match {
      case STRING_LITERAL   => parseStringValue(s)
      case NUMBER_LITERAL   => parseIntegerValue(s, context)
      case BOOLEAN_LITERAL  => parseBooleanValue(s, context)
      case it => interrupt(context, "Unknown literal token: %s", it)
    }

    Some(value)
  }

  private def parseStringValue(s: String) = {
    val text = s.substring(1, s.length - 1)
    StringValue(text)
  }

  private def parseIntegerValue(s: String, context: Context) = {
    try {
      val i = Integer.parseInt(s)
      IntegerValue(i)
    } catch {
      case _: NumberFormatException => interrupt(context, "Wrong integer literal: %s", s)
    }
  }

  private def parseBooleanValue(s: String, context: Context) = {
    val b = s match {
      case "true"   => true
      case "false"  => false
      case _        => interrupt(context, "Wrong boolean literal: %s", s)
    }
    BooleanValue(b)
  }
}