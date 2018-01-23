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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue}

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
