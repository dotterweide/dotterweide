/*
 *  WhileEvaluator.scala
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
import com.pavelfatin.toyide.languages.toy.node.While

trait WhileEvaluator extends ConditionHolderEvaluator { self: While =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    while (evaluateCondition(context, output)) {
      block.foreach {
        _.evaluate(context, output)
      }
    }
    None
  }
}