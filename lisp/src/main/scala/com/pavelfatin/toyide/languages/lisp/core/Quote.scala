/*
 *  Quote.scala
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
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, ListValue, StringValue, SymbolValue}

object Quote extends CoreFunction("quote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(expression) => expression
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("quote"), value))
}

object Unquote extends CoreFunction("unquote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(_) => error("not applicable outside syntax quoting", environment)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("unquote"), value))

  def unapply(expression: Expression): Option[Expression] = expression match {
    case ListValue(Seq(SymbolValue("unquote"), value)) => Some(value)
    case _ => None
  }
}

object UnquoteSplicing extends CoreFunction("unquote-splicing", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(_) => error("not applicable outside syntax quoting", environment)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("unquote-splicing"), value))

  def unapply(expression: Expression): Option[Expression] = expression match {
    case ListValue(Seq(SymbolValue("unquote-splicing"), value)) => Some(value)
    case _ => None
  }
}

object GenSym extends CoreFunction("gensym") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): SymbolValue = arguments match {
    case Seq(StringValue(prefix)) => SymbolValue(prefix + "_" + environment.nextId())
    case _ => expected("prefix", arguments, environment)
  }
}

object Quasiquote extends CoreFunction("quasiquote", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = arguments match {
    case Seq(expression) =>
      val names = new NameGenerator(environment)
      def quasiquote(expression: Expression): Expression = expression match {
        case Unquote(value) => value.eval(environment, output)
        case UnquoteSplicing(_) => error("splicing outside of list", environment)
        case list @ ListValue(values) =>
          val splicedValues = values.flatMap {
            case UnquoteSplicing(value) => value.eval(environment, output) match {
              case ListValue(children) => children
              case v => error("splicing of non-list value: " + v.presentation, environment)
            }
            case value => Seq(quasiquote(value))
          }
          ListValue(splicedValues, list.place)
        case SymbolValue(s) if s.endsWith("#") =>
          val prefix = s.substring(0, s.length - 1)
          SymbolValue(names.unique(prefix))
        case value => value
      }
      quasiquote(expression)
    case _ => expected("expression", arguments, environment)
  }

  def apply(value: Expression): Expression = ListValue(Seq(SymbolValue("quasiquote"), value))
}
