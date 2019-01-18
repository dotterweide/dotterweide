/*
 *  Function.scala
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
import dotterweide.languages.lisp.value.{Environment, Expression, FunctionValue, ListValue, SymbolValue, UserFunction}

object Fn extends CoreFunction("fn", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): UserFunction = {
    def createFunction(name: Option[String], parameterList: ListValue, expressions: Seq[Expression]) = {
      val parameters = Parameters.from(parameterList).fold(error(_, environment), identity)
      new UserFunction(name, parameters, expressions, environment.locals)
    }

    arguments match {
      case Seq(parameterList: ListValue, expressions @ _*) =>
        createFunction(None, parameterList, expressions)
      case Seq(SymbolValue(name), parameterList: ListValue, expressions @ _*) =>
        createFunction(Some(name), parameterList, expressions)
      case _ => expected("name? [params*] exprs*", arguments, environment)
    }
  }
}

object Apply extends CoreFunction("apply") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(f: FunctionValue, ListValue(expressions)) =>
      f.apply(expressions, environment, output)
    case _ => expected("f args", arguments, environment)
  }
}
