/*
 *  Macro.scala
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
import dotterweide.languages.lisp.parameters.Parameters
import dotterweide.languages.lisp.value.{Environment, Expression, ListValue, MacroFunction, SymbolValue}

object Eval extends CoreFunction("eval") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(expression) => expression.eval(environment, output)
    case _ => expected("expression", arguments, environment)
  }
}

object Macro extends CoreFunction("macro", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): MacroFunction = {
    def createMacro(name: Option[String], parameterList: ListValue, expressions: Seq[Expression]) = {
      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)
      new MacroFunction(name, parameters, expressions, environment.locals)
    }

    arguments match {
      case Seq(parameterList: ListValue, expressions @ _*) =>
        createMacro(None, parameterList, expressions)
      case Seq(SymbolValue(name), parameterList: ListValue, expressions @ _*) =>
        createMacro(Some(name), parameterList, expressions)
      case _ => expected("[params*] exprs*", arguments, environment)
    }
  }
}

object Macroexpand extends CoreFunction("macroexpand") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(ListValue(Seq(head, tail @ _*))) => head.eval(environment, output) match {
      case m: MacroFunction => m.expand(tail, environment, output)
      case _ => error("macro application expected", environment)
    }
    case _ => expected("list", arguments, environment)
  }
}
