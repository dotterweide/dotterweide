/*
 *  ProgramNode.scala
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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.compiler.{Labels, TranslationException}
import com.pavelfatin.toyide.interpreter.Context
import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.value.{Environment, Expression, ListValue}
import com.pavelfatin.toyide.node.NodeImpl

class ProgramNode extends NodeImpl("program") {
  def expressions: Seq[ExpressionNode] = children.filterBy[ExpressionNode]

  override def evaluate(context: Context, output: Output): Option[Expression] = {
    val environment = Library.instance.createEnvironment()
    val value = evaluate(ProgramNode.Source, environment, output)
    Some(value)
  }

  def evaluate(source: String, environment: Environment, output: Output): Expression = {
    val values = expressions.map(_.read(source))
    values.foldLeft[Expression](ListValue.Empty)((_, x) => x.eval(environment, output))
  }

  override def translate(className: String, labels: Labels) =
    throw TranslationException("Translation to bytecode is not yet implemented.")
}

object ProgramNode {
  private val Source = "User"
}