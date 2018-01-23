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

import com.pavelfatin.toyide.languages.lisp.value.{Expression, ListValue, SymbolValue}

import scala.util.control.Exception

trait Parameters {
  def bind(argument: Expression): Either[String, Map[String, Expression]]

  def presentation: String

  def symbols: Seq[String]
}

object Parameters {
  private val Separator =  "&"

  def from(expression: Expression): Either[String, Parameters] =
    Exception.catching(classOf[MalformedParametersException])
      .either(from0(expression)).left.map(_.getMessage)

  private def from0(expression: Expression): Parameters = {
    val parameters = toParameters(expression)

    val duplicates = duplicatesIn(parameters.symbols)

    if (duplicates.nonEmpty)
      error("Duplicate parameters: " + duplicates.mkString(" "))

    parameters
  }

  private def toParameters(expression: Expression): Parameters = expression match {
    case SymbolValue(name) => new NamedParameter(name)

    case ListValue(children) =>
      val (singularExpressions, pluralExpression) = split(children)

      val singularParameters = singularExpressions.map(toParameters)
      val pluralParameters = pluralExpression.map(toParameters)

      new ParameterList(singularParameters, pluralParameters)

    case e => error("Non-symbol in parameter list: " + e)
  }

  private def split(expressions: Seq[Expression]): (Seq[Expression], Option[Expression]) = {
    val (singularExpressions, restExpressions) = expressions.span(!isSeparator(_))

    val pluralExpressions = restExpressions.drop(1)

    if (pluralExpressions.exists(isSeparator) || pluralExpressions.size > 1)
      error("Single variable-length parameter expected, but was: " + pluralExpressions.mkString(" "))

    (singularExpressions, pluralExpressions.headOption)
  }

  private val isSeparator: Expression => Boolean = {
    case SymbolValue(Separator) => true
    case _ => false
  }

  private def duplicatesIn[A](seq: Seq[A]): Seq[A] =
    seq.groupBy(identity).filter(_._2.size > 1).keys.toSeq

  private def error(message: String) = throw new MalformedParametersException(message)

  private class MalformedParametersException(message: String) extends Exception(message)
}
