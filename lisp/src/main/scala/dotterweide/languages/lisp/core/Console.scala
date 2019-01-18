/*
 *  Console.scala
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

package dotterweide.languages.lisp.core

import dotterweide.Output
import dotterweide.languages.lisp.value.{CharacterValue, Environment, Expression, ListValue}

object Print extends CoreFunction("print") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    output.print(Expression.format(arguments))
    ListValue.Empty
  }
}

object PrintLn extends CoreFunction("println") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    output.print(Expression.format(arguments) + "\n")
    ListValue.Empty
  }
}

object Trace extends CoreFunction("trace") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(v) => output.print(v.presentation); v
    case _ => expected("value", arguments, environment)
  }
}

object Format extends CoreFunction("format") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = {
    ListValue(Expression.format(arguments).map(CharacterValue))
  }
}
