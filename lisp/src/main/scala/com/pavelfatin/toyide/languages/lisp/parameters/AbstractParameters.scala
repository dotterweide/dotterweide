/*
 *  AbstractParameters.scala
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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.Expression

import scala.util.control.Exception._

trait AbstractParameters extends Parameters {
  final def bind(argument: Expression): Either[String, Map[String, Expression]] =
    catching(classOf[BindingException])
      .either(bind0(argument)).left.map(_.getMessage)

  protected def bind0(argument: Expression): Map[String, Expression]

  protected def error(message: String): Nothing = throw new BindingException(message)

  private class BindingException(message: String) extends Exception(message)
}
