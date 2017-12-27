package de.sciss.scalalang

import java.awt.Color

import com.pavelfatin.toyide.editor.{AbstractColoring, Attributes, Coloring, Style, Weight}
import com.pavelfatin.toyide.lexer.TokenKind

class ScalaColoring(colors: Map[String, Color]) extends AbstractColoring(colors) {
  def attributesFor(kind: TokenKind): Attributes = {
    val foreground  = apply(colorId(kind))
    val weight      = weightFor(kind)
    val style       = styleFor(kind)
    Attributes(foreground, None, weight, style, underlined = false)
  }

  private def colorId(kind: TokenKind) = kind match {
    case _ => Coloring.TextForeground
  }

  private def weightFor(token: TokenKind) = token match {
    case _ => Weight.Normal
  }

  private def styleFor(token: TokenKind) = token match {
    case _ => Style.Ordinary
  }
}