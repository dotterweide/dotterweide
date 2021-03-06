/*
 *  ToyEvaluable.scala
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

package dotterweide.languages.toy.interpreter

import dotterweide.Extensions._
import dotterweide.interpreter.{Context, EvaluationException, Place}
import dotterweide.languages.toy.node.FunctionDeclaration
import dotterweide.node.Node

trait ToyEvaluable { self: Node =>
  protected def interrupt(context: Context, message: String, values: Any*) =
    throw EvaluationException(message.format(values: _*), place :: context.trace.toList)

  protected def wrap[A](context: => Context)(action: => A): A = {
    try {
      action
    } catch {
      case e: IllegalStateException => interrupt(context, e.getMessage)
    }
  }

  protected def place: Place = {
    val enclosure = self.parents.findBy[FunctionDeclaration].map(_.identifier)
    val line      = self.span.source.take(self.span.start).count(_ == '\n')
    Place(enclosure, line)
  }
}