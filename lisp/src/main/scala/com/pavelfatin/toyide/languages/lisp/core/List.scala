/*
 *  List.scala
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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, ListValue, SymbolValue}

object NewList extends CoreFunction("list") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output) = ListValue(arguments)

  def apply(values: Seq[Expression]): Expression = ListValue(SymbolValue("list") +: values)
}

object Cons extends CoreFunction("cons") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(x, ListValue(l)) => ListValue(x :: l)
    case _ => expected("x list", arguments, environment)
  }
}

object First extends CoreFunction("first") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(ListValue(l)) =>
      if (l.isEmpty) error("first on empty list", environment) else l.head
    case _ => expected("list", arguments, environment)
  }
}

object Rest extends CoreFunction("rest") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(ListValue(l)) =>
      if (l.isEmpty) error("rest on empty list", environment) else ListValue(l.tail)
    case _ => expected("list", arguments, environment)
  }
}

