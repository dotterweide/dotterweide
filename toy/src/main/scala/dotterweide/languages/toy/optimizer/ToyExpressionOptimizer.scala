/*
 *  ToyExpressionOptimizer.scala
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

package dotterweide.languages.toy.optimizer

import dotterweide.interpreter.{ContextImpl, EvaluationException}
import dotterweide.languages.toy.interpreter.ToyValue.StringValue
import dotterweide.languages.toy.node.ToyExpression

trait ToyExpressionOptimizer { self: ToyExpression =>
  override lazy val optimized: Option[String] = {
    if (self.constant) {
      try {
        self.evaluate(new ContextImpl(), NullOutput) collect {
          case v: StringValue => "\"%s\"".format(v.presentation)
          case v => v.presentation
        }
      } catch {
        case _: EvaluationException => None
      }
    } else {
      None
    }
  }
}