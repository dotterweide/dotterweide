/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
