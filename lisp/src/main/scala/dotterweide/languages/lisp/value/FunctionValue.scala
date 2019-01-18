/*
 *  FunctionValue.scala
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

package dotterweide.languages.lisp.value

import dotterweide.Output
import dotterweide.languages.lisp.LispType
import dotterweide.node.NodeType

trait FunctionValue extends Expression {
  def name: Option[String]

  def valueType: NodeType = LispType.FunctionType

  def eval(environment: Environment, output: Output): Expression =
    environment.interrupt("Evaluation of " + presentation)

  def isLazy: Boolean

  def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression

  protected def error(message: String, environment: Environment): Nothing =
    environment.interrupt(presentation + ": " + message)
}
