package de.sciss.scalalang

import dotterweide.formatter.Distance.Joint
import dotterweide.formatter.{Distance, Format}
import dotterweide.lexer.TokenKind

object ScalaFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind): Distance = Joint

  def indentDeltaFor(a: TokenKind, b: TokenKind): Int = 0
}