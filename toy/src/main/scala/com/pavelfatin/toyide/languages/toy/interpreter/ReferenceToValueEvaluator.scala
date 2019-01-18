/*
 *  ReferenceToValueEvaluator.scala
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

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{Context, Value}
import com.pavelfatin.toyide.languages.toy.node._

trait ReferenceToValueEvaluator extends ToyEvaluable { self: ReferenceToValue =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    val node = target.getOrElse(
      interrupt(context, "Target value not found: %s", identifier))

    def local = node match {
      case v: VariableDeclaration => v.local
      case _: Parameter           => true
      case _ => interrupt(context, "Non-value target for reference %s: %s", identifier, node.span.text)
    }

    wrap(context) {
      Some(context.get(local, identifier))
    }
  }
}