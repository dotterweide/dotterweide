/*
 *  UserFunction.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters

class UserFunction(val name: Option[String], parameters: Parameters, expressions: Seq[Expression], closure: Map[String, Expression])
  extends FunctionValue with TailCalls {

  def isLazy = false

  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = {
    val bindings = parameters.bind(ListValue(arguments)).fold(error(_, environment), identity)

    val initialEnvironment = environment.clearLocals.addLocals(closure ++ bindings)

    withTailCalls(parameters, initialEnvironment) { env =>
      expressions.map(_.eval(env, output)).lastOption.getOrElse(ListValue.Empty)
    }
  }

  def presentation: String = {
    val prefix = name.map(_ + "_").getOrElse("")
    prefix + "fn" + parameters.presentation
  }
}
