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