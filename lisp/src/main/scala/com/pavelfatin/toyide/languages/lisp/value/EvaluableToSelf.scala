/*
 *  EvaluableToSelf.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.DelegateValue
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.node.NodeType

trait EvaluableToSelf extends Expression {
  def eval(environment: Environment, output: Output): Expression = this
}

case class BooleanValue(content: Boolean) extends DelegateValue[Boolean] with EvaluableToSelf {
  def valueType: NodeType = LispType.BooleanType
}

case class IntegerValue(content: Int) extends DelegateValue[Int] with EvaluableToSelf {
  def valueType: NodeType = LispType.IntegerType
}

case class CharacterValue(content: Char) extends DelegateValue[Char] with EvaluableToSelf {
  def valueType: NodeType = LispType.CharacterType

  override def presentation: String = {
    val s = content match {
      case ' ' => "space"
      case '\t' => "tab"
      case '\r' => "return"
      case '\n' => "newline"
      case c => c
    }
    "\\" + s
  }
}
