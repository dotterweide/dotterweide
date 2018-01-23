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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, FunctionValue}

abstract class CoreFunction(val name0: String, val isLazy: Boolean = false) extends FunctionValue {
  def name = Some(name0)

  def presentation: String = "core." + name0

  protected def expected(parameters: String, arguments: Seq[Expression], environment: Environment): Nothing =
    environment.interrupt("%s syntax: (%s %s), application: (%s %s)"
      .format(presentation, name0, parameters, name0, Expression.format(arguments)))
}

object CoreFunction {
  private val Functions: Set[CoreFunction] = Set(
    IsInteger, IsBoolean, IsCharacter, IsSymbol, IsFunction, IsList,
    Quote, Quasiquote, Unquote, GenSym,
    Eval, Macro, Macroexpand,
    Def, Let, Fn, Apply,
    Do, If, Error, Loop, Recur,
    Print, PrintLn, Trace, Format,
    Dir, Exists, Directory, Open, Listen, Read, Write, Flush, Close,
    NewList, Cons, First, Rest,
    Add, Sub, Mul, Div, Mod,
    Gt, GtEq, Lt, LtEq, Eq,
    And, Or, Not)

  val NameToFunction: Map[String, CoreFunction] = Functions.map(f => (f.name0, f)).toMap

  val Names: Set[String] = NameToFunction.keySet
}
