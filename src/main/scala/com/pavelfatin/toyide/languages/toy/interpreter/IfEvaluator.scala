/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{Context, Value}
import com.pavelfatin.toyide.languages.toy.node.If

trait IfEvaluator extends ConditionHolderEvaluator { self: If =>
  override def evaluate(context: Context, output: Output): Option[Value] = {
    if (evaluateCondition(context, output)) {
      val scope = block.getOrElse(
        interrupt(context, "If block not found %s", span.text))
      scope.evaluate(context, output)
    } else {
      elseBlock.foreach {
        _.evaluate(context, output)
      }
    }
    None
  }
}