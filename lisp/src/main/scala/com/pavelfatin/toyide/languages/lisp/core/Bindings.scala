/*
 *  Bindings.scala
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
