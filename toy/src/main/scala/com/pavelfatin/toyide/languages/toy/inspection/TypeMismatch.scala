/*
 *  TypeMismatch.scala
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