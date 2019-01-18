/*
 *  Definition.scala
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
import dotterweide.languages.lisp.value.{Environment, Expression, ListValue, SymbolValue}

object Def extends CoreFunction("def", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(SymbolValue(name), expression) =>
      environment.setGlobal(name, expression.eval(environment, output))
      ListValue.Empty
    case _ => expected("symbol expression", arguments, environment)
  }
}

object Let extends CoreFunction("let", isLazy = true) with Bindings {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(ListValue(elements), expressions @ _*) =>
      val env = bind(elements, environment, output)
      expressions.map(_.eval(env, output)).lastOption.getOrElse(ListValue.Empty)
    case _ => expected("[bindings*] exprs*", arguments, environment)
  }
}
