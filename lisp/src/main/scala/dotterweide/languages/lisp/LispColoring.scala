/*
 *  LispColoring.scala
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

package dotterweide.languages.lisp

import java.awt.Color

import dotterweide.editor.{AbstractColoring, Attributes, Coloring, Style, Weight}
import dotterweide.languages.lisp.LispTokens._
import dotterweide.lexer.TokenKind

class LispColoring(colors: Map[String, Color]) extends AbstractColoring(colors) {
  def attributesFor(kind: TokenKind): Attributes = {
    val foreground  = apply(colorId(kind))
    val weight      = weightFor(kind)
    val style       = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case COMMENT            => Coloring.Comment
    case BOOLEAN_LITERAL    => Coloring.BooleanLiteral
    case INTEGER_LITERAL    => Coloring.IntegerLiteral
    case CHARACTER_LITERAL  => Coloring.CharLiteral
    case STRING_LITERAL     => Coloring.StringLiteral
    case PREDEFINED_SYMBOL  => Coloring.Keyword
    case _                  => Coloring.TextForeground
  }

  private def weightFor(token: TokenKind) = token match {
    case BOOLEAN_LITERAL    => Weight.Bold
    case STRING_LITERAL     => Weight.Bold
    case PREDEFINED_SYMBOL  => Weight.Bold
    case _                  => Weight.Normal
  }

  private def styleFor(token: TokenKind) = token match {
    case COMMENT  => Style.Italic
    case _        => Style.Ordinary
  }
}
