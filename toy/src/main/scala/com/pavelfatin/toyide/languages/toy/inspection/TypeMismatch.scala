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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.inspection.{Inspection, Mark}
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.languages.toy.ToyType.VoidType
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.node._

object TypeMismatch extends Inspection {
  val Message: (String, String) => String = "Type mismatch, expected: %s, actual: %s".format(_: String, _: String)

  val ReturnFromVoidFunctionMessage = "Cannot return a value from a function with void return type"

  val MissingReturnValueMessage = "Missing return value"

  // for some reason compiler complains when the following is written in a single "match"
  def inspect(node: Node): Seq[Mark] = node match {
    case r @ Return(None, Some(expected)) if expected != VoidType =>
      Mark(r, MissingReturnValueMessage) :: Nil
    case r @ Return(Some(_), Some(expected)) if expected == ToyType.VoidType =>
      Mark(r, ReturnFromVoidFunctionMessage) :: Nil
    case _ => node match {
      case ExpressionHolder(Some(exp @ Expression(actual)), Some(expected)) if expected != actual =>
        Mark(exp, Message(expected.presentation, actual.presentation)) :: Nil
      case _ => Nil
    }
  }
}