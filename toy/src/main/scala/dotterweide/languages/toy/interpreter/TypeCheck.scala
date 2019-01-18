/*
 *  TypeCheck.scala
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
import dotterweide.languages.toy.ToyType.{BooleanType, IntegerType, StringType, VoidType}
import dotterweide.languages.toy.interpreter.ToyValue._
import dotterweide.node.{Expression, NodeType}

trait TypeCheck extends ToyEvaluable with Expression {
  abstract override def evaluate(context: Context, output: Output): Option[Value] = {
    val result = super.evaluate(context, output)
    check(context, nodeType, result)
    result
  }

  private def check(context: Context, nodeType: Option[NodeType], value: Option[Value]): Unit = {
    nodeType match {
      case Some(t) =>
        (t, value) match {
          case (StringType, Some(_: StringValue)) =>
          case (IntegerType, Some(_: IntegerValue)) =>
          case (BooleanType, Some(_: BooleanValue)) =>
          case (VoidType, None) =>
          case _ => interrupt(context, "Type case exception, expected: %s, actual: %s", t, value)
        }
      case None => interrupt(context, "Evaluation of node with unknown type: %s", span.text)
    }
  }
}