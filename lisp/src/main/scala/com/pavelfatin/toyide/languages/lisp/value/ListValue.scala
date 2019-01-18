/*
 *  ListValue.scala
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
import com.pavelfatin.toyide.interpreter.{DelegateValue, Place}
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.node.NodeType

class ListValue private (val content: List[Expression], val place: Option[Place])
  extends DelegateValue[List[Expression]] with Expression {

  def valueType: NodeType = LispType.ListType

  private val macroExpansionCache = new SingleValueCache[(MacroFunction, Seq[Expression]), Expression]()

  def eval(environment: Environment, output: Output): Expression = {
    if (content.nonEmpty) content.head.eval(environment, output) match {
      case m: MacroFunction =>
        val arguments = content.tail
        val expansion = macroExpansionCache.getOrUpdate((m, arguments)) {
          m.expand(arguments, environment.inFrame(place), output)
        }
        expansion.eval(environment, output)
      case f: FunctionValue =>
        val expressions = content.tail
        val arguments = if (f.isLazy) expressions else expressions.map(_.eval(environment, output))
        f.apply(arguments, environment.inFrame(place), output)
      case v => environment.interrupt("Cannot apply to " + v.presentation, place)
    } else {
      environment.interrupt("Empty application", place)
    }
  }

  override def presentation: String = content.map(_.presentation).mkString("(", " ", ")")
}

object ListValue {
  val Empty = ListValue(Nil)

  def apply(expressions: Seq[Expression], place: Option[Place] = None): ListValue =
    new ListValue(expressions.toList, place)

  def unapply(list: ListValue): Option[List[Expression]] = Some(list.content)
}
