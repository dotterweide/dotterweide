/*
 *  Comparison.scala
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

package dotterweide.languages.lisp.core

import dotterweide.Output
import dotterweide.languages.lisp.value.{BooleanValue, CharacterValue, Environment, Expression, IntegerValue, ListValue}

object Gt extends CoreFunction(">") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 > i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object GtEq extends CoreFunction(">=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 >= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Lt extends CoreFunction("<") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 < i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object LtEq extends CoreFunction("<=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(IntegerValue(i1), IntegerValue(i2)) => BooleanValue(i1 <= i2)
    case _ => expected("i1 i2", arguments, environment)
  }
}

object Eq extends CoreFunction("=") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) =
    BooleanValue(apply0(arguments, environment, output))

  private def apply0(arguments: Seq[Expression], environment: Environment, output: Output): Boolean = {
    arguments match {
      case Seq(IntegerValue(i1), IntegerValue(i2)) => i1 == i2
      case Seq(CharacterValue(c1), CharacterValue(c2)) => c1 == c2
      case Seq(BooleanValue(b1), BooleanValue(b2)) => b1 == b2
      case Seq(ListValue(l1), ListValue(l2)) =>
        l1.size == l2.size && l1.zip(l2).map(p => Seq(p._1, p._2)).forall(apply0(_, environment, output))
      case _ => expected("v1 v2", arguments, environment)
    }
  }
}
