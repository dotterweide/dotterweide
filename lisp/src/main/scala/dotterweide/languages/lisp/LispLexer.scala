/*
 *  LispLexer.scala
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

import dotterweide.languages.lisp.LispTokens._
import dotterweide.languages.lisp.TokenIterator.Operators
import dotterweide.languages.lisp.core.CoreFunction
import dotterweide.languages.lisp.library.Library
import dotterweide.lexer.TokenKind._
import dotterweide.lexer.{AbstractTokenIterator, Lexer, Token}

object LispLexer extends Lexer {
  def analyze(input: CharSequence): Iterator[Token] = new TokenIterator(input)
}

private class TokenIterator(input: CharSequence) extends AbstractTokenIterator(input) {
  def next(): Token = {
    if (char == ';') {
        mark()
        skip(c => c != '\n')
        return Token(COMMENT, marked)
    }

    if (char == ',' ) return Token(COMMA, captureChar)
    if (char == '#' ) return Token(HASH , captureChar)
    if (char == '\'') return Token(QUOTE, captureChar)

    if (char == '~') {
      if (isAhead('@')) {
        return Token(TILDE_AT, captureChars(2))
      } else {
        return Token(TILDE, captureChar)
      }
    }

    if (char == '`') return Token(BACKQUOTE, captureChar)

    if (char == '(') return Token(LPAREN, captureChar)
    if (char == ')') return Token(RPAREN, captureChar)

    if (char == '[') return Token(LBRACKET, captureChar)
    if (char == ']') return Token(RBRACKET, captureChar)

    if (char.isWhitespace) return Token(WS, capture(_.isWhitespace))

    if (char.isDigit) return Token(INTEGER_LITERAL, capture(_.isDigit))

    if (char == '-' && isAhead(_.isDigit)) {
      mark()
      advance()
      skip(_.isDigit)
      return Token(INTEGER_LITERAL, marked)
    }

    if (char == '"') {
      mark()
      advance()
      if (!hasNext) return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      skip(c => c != '"' && c != '\n')
      if (!hasNext || char != '"') return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      advance()
      return Token(STRING_LITERAL, marked)
    }

    if (char == '\\') {
      mark()
      advance()
      if (!hasNext) return Token(CHARACTER_LITERAL, marked, Some("Empty character"))
      if (isAhead("space")) {
        advance(5)
      } else if (isAhead("tab")) {
        advance(3)
      } else if (isAhead("return")) {
        advance(6)
      } else if (isAhead("newline")) {
        advance(7)
      } else {
        advance()
      }
      return Token(CHARACTER_LITERAL, marked)
    }

    if (char.isLetter || Operators.contains(char)) {
      val span = capture(c => c.isLetterOrDigit || Operators.contains(c) || c == '#')

      val text = span.text

      if (text == "true" || text == "false") return Token(BOOLEAN_LITERAL, span)

      val predefined = CoreFunction.Names.contains(text) || Library.instance.symbols.contains(text)

      val kind = if (predefined) PREDEFINED_SYMBOL else CUSTOM_SYMBOL

      return Token(kind, span)
    }

    Token(UNKNOWN, captureChar, Some("Unknown token"))
  }
}

object TokenIterator {
  private val Operators = Set('+', '-', '*', '/', '?', '>', '<', '=', '&', '_', '%', '\'')
}
