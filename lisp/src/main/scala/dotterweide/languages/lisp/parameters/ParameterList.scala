/*
 *  ParameterList.scala
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

package dotterweide.languages.lisp.parameters

import dotterweide.languages.lisp.value.{Expression, ListValue}

import scala.Function.const

private class ParameterList(singularParameters: Seq[Parameters], pluralParameter: Option[Parameters]) extends AbstractParameters {
  protected def bind0(argument: Expression): Map[String, Expression] = {
    val arguments = argument match {
      case ListValue(expressions) => expressions
      case e => error("Cannot deconstruct non-list value: " + e.presentation)
    }

    if (arguments.size < singularParameters.size) {
      error("Not enough arguments: " + Expression.format(arguments))
    }

    if (arguments.size > singularParameters.size && pluralParameter.isEmpty) {
      error("Too many arguments: " + Expression.format(arguments))
    }

    val (singularArguments, restArguments) = arguments.splitAt(singularParameters.size)

    val singularPairs = singularParameters.zip(singularArguments)
    val pluralPair = pluralParameter.map((_, ListValue(restArguments)))

    val pairs = singularPairs ++ pluralPair.toSeq

    val bindings = pairs.flatMap {
      case (pattern, initializer) => pattern.bind(initializer).fold(error, identity)
    }

    bindings.toMap
  }

  def symbols: Seq[String] = (singularParameters ++ pluralParameter.toSeq).flatMap(_.symbols)

  def presentation: String = singularParameters.size + pluralParameter.map(const("*")).mkString
}
