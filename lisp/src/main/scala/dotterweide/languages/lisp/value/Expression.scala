/*
 *  Expression.scala
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

package dotterweide.languages.lisp.value

import dotterweide.Output
import dotterweide.interpreter.Value

trait Expression extends Value {
  def eval(environment: Environment, output: Output): Expression

  override def toString: String = "%s: %s".format(presentation, valueType.presentation)
}

object Expression {
  def format(expressions: Seq[Expression]): String =
    expressions.map(format).mkString(" ")

  private def format(expression: Expression): String = expression match {
    case StringValue(s) => s
    case value => value.presentation
  }
}