/*
 *  ToyStyling.scala
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

package dotterweide.languages.toy

import java.awt.Color

import dotterweide.editor.{AbstractStyling, Attributes, Style, Styling, Weight}
import dotterweide.languages.toy.ToyTokens._
import dotterweide.lexer.TokenKind

class ToyStyling(colors: Map[String, Color])
  extends AbstractStyling(colors) {

  def attributesFor(kind: TokenKind): Attributes = {
    val foreground  = apply(colorId(kind))
    val weight      = weightFor(kind)
    val style       = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case COMMENT                      => Styling.Comment
    case BOOLEAN_LITERAL              => Styling.BooleanLiteral
    case NUMBER_LITERAL               => Styling.IntegerLiteral
    case STRING_LITERAL               => Styling.StringLiteral
    case it if Keywords .contains(it) => Styling.Keyword
    case it if Types    .contains(it) => Styling.Keyword
    case _                            => Styling.TextForeground
  }

  private def weightFor(token: TokenKind) = token match {
    case BOOLEAN_LITERAL              => Weight.Bold
    case STRING_LITERAL               => Weight.Bold
    case it if Keywords .contains(it) => Weight.Bold
    case it if Types    .contains(it) => Weight.Bold
    case _                            => Weight.Normal
  }

  private def styleFor(token: TokenKind) = token match {
    case COMMENT  => Style.Italic
    case _        => Style.Ordinary
  }
}