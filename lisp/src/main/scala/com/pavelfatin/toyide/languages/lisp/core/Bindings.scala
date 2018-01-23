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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.parameters.Parameters
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression}

trait Bindings { self: CoreFunction =>
  protected def bind(elements: Seq[Expression], environment: Environment, output: Output): Environment = {
    if (elements.size % 2 > 0) error("an even number of expressions is required", environment)

    val pairs = elements.grouped(2).toSeq.map { case Seq(pattern, initializer) =>
      val parameters = Parameters.from(pattern).fold(error(_, environment), identity)
      (parameters, initializer)
    }

    pairs.foldLeft(environment) { case (env, (parameters, initializer)) =>
      val bindings = parameters.bind(initializer.eval(env, output)).fold(error(_, env), identity)
      env.addLocals(bindings)
    }
  }
}
