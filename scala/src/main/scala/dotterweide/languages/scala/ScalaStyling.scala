/*
 *  ScalaStyling.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import java.awt.Color

import dotterweide.editor.{AbstractStyling, Attributes, Style, Styling, Weight}
import dotterweide.languages.scala.node.ScalaTokens._
import dotterweide.lexer.TokenKind

class ScalaStyling(colors: Map[String, Color])
  extends AbstractStyling(colors) {

  def attributesFor(kind: TokenKind): Attributes = {
    val foreground  = apply(colorId(kind))
    val weight      = weightFor(kind)
    val style       = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case TRUE | FALSE                             => Styling.BooleanLiteral
    case INTEGER_LITERAL | FLOATING_POINT_LITERAL => Styling.IntegerLiteral
    case STRING_LITERAL | STRING_PART             => Styling.StringLiteral
    case CHARACTER_LITERAL | SYMBOL_LITERAL       => Styling.CharLiteral
    case it if Comments.contains(it)              => Styling.Comment
    case it if Keywords.contains(it)              => Styling.Keyword
//    case it if Types    .contains(it) => Coloring.Keyword
    case _ => Styling.TextForeground
  }

  private def weightFor(token: TokenKind) = token match {
    case TRUE | FALSE                       => Weight.Bold
    case STRING_LITERAL | STRING_PART       => Weight.Bold
    case CHARACTER_LITERAL | SYMBOL_LITERAL => Weight.Bold
    case it if Keywords .contains(it)       => Weight.Bold
//    case it if Types    .contains(it) => Weight.Bold
    case _                                  => Weight.Normal
  }

  private def styleFor(token: TokenKind) = token match {
    case it if Comments.contains(it)  => Style.Italic
    case _                            => Style.Ordinary
  }
}