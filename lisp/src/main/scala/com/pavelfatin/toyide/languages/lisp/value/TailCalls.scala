/*
 *  TailCalls.scala
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

import com.pavelfatin.toyide.languages.lisp.parameters.Parameters

import scala.annotation.tailrec

trait TailCalls { self: FunctionValue =>
  protected def withTailCalls(parameters: Parameters, initialEnvironment: Environment)
                             (evaluateIn: Environment => Expression): Expression = {
    @tailrec
    def evaluateIterativelyIn(environment: Environment): Expression = evaluateIn(environment) match {
      case RecurValue(arguments) =>
        val bindings = parameters.bind(ListValue(arguments)).fold(error(_, environment), identity)
        evaluateIterativelyIn(environment.addLocals(bindings))
      case expr => expr
    }

    evaluateIterativelyIn(initialEnvironment)
  }
}
