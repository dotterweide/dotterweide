package de.sciss.scalalang

import com.pavelfatin.toyide.formatter.Distance.Joint
import com.pavelfatin.toyide.formatter.{Distance, Format}
import com.pavelfatin.toyide.lexer.TokenKind

object ScalaFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind): Distance = Joint

  def indentDeltaFor(a: TokenKind, b: TokenKind): Int = 0
}