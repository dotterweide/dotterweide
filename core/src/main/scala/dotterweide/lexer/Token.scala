/*
 *  Token.scala
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

package dotterweide.lexer

import dotterweide.Span

case class Token(kind: TokenKind, span: Span, problem: Option[String] = None) {
  override def toString: String = {
    val prefix = if (problem.isDefined) "error: " else ""
    val s = if (kind.data) span.text else kind.name
    prefix + s
  }

  def toCompleteString: String = {
    val prefix = if (problem.isDefined) "error: " else ""
    prefix + "%s(%s)".format(kind.name, span.text)
  }
}
