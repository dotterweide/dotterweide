/*
 *  NamedParameter.scala
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

private class NamedParameter(name: String) extends AbstractParameters {
  protected def bind0(argument: Expression): Map[String, Expression] =
    if (isPlaceholder) Map.empty else Map(name -> argument)

  private def isPlaceholder: Boolean = name == NamedParameter.Placeholder

  def symbols: Seq[String] = if (isPlaceholder) Nil else name :: Nil

  def presentation = throw new UnsupportedOperationException()
}

private object NamedParameter {
  private val Placeholder = "_"
}