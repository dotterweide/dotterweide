/*
 *  CoreFunction.scala
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
