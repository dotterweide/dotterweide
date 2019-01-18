/*
 *  ToyValue.scala
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

import com.pavelfatin.toyide.interpreter.DelegateValue
import com.pavelfatin.toyide.languages.toy.ToyType.{BooleanType, IntegerType, StringType}
import com.pavelfatin.toyide.node.NodeType

sealed trait ToyValue[+A] extends DelegateValue[A]

object ToyValue {
  case class StringValue(content: String) extends ToyValue[String] {
    def valueType: NodeType = StringType
  }

  case class IntegerValue(content: Int) extends ToyValue[Int] {
    def valueType: NodeType = IntegerType
  }

  case class BooleanValue(content: Boolean) extends ToyValue[Boolean] {
    def valueType: NodeType = BooleanType
  }
}