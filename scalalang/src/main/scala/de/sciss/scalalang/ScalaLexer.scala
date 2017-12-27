package de.sciss.scalalang

import com.pavelfatin.toyide.Span
import com.pavelfatin.toyide.lexer.{Lexer, Token, TokenKind}

object ScalaLexer extends Lexer {
  def analyze(input: CharSequence): Iterator[Token] =
    Iterator.single(Token(TokenKind("foo"), Span(input, 0, input.length()), problem = Some("not yet parsed")))
}
