package de.sciss.scalalang

import java.awt.Color

import com.pavelfatin.toyide.editor.{AbstractColoring, Attributes, Coloring, Style, Weight}
import com.pavelfatin.toyide.lexer.TokenKind
import de.sciss.scalalang.node.ScalaTokens._

class ScalaColoring(colors: Map[String, Color]) extends AbstractColoring(colors) {
  def attributesFor(kind: TokenKind): Attributes = {
    val foreground  = apply(colorId(kind))
    val weight      = weightFor(kind)
    val style       = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case TRUE | FALSE                             => Coloring.BooleanLiteral
    case INTEGER_LITERAL | FLOATING_POINT_LITERAL => Coloring.IntegerLiteral
    case STRING_LITERAL | STRING_PART             => Coloring.StringLiteral
    case CHARACTER_LITERAL | SYMBOL_LITERAL       => Coloring.CharLiteral
    case it if Comments.contains(it)              => Coloring.Comment
    case it if Keywords.contains(it)              => Coloring.Keyword
//    case it if Types    .contains(it) => Coloring.Keyword
    case _ => Coloring.TextForeground
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