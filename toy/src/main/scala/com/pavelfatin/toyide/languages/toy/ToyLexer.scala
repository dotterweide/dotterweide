/*
 *  ToyLexer.scala
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

package com.pavelfatin.toyide.languages.toy

import ToyTokens._
import com.pavelfatin.toyide.lexer.TokenKind._
import com.pavelfatin.toyide.lexer.{AbstractTokenIterator, Lexer, Token}

object ToyLexer extends Lexer {
  def analyze(input: CharSequence): Iterator[Token] = new TokenIterator(input)
}

private class TokenIterator(input: CharSequence) extends AbstractTokenIterator(input) {
  def next(): Token = {
    if (char == '=') {
      if (isAhead('=')) {
        return Token(EQ_EQ, captureChars(2))
      }
      return Token(EQ, captureChar)
    }

    if (char == '!' && isAhead('=')) return Token(BANG_EQ, captureChars(2))

    if (char == ':') return Token(COLON, captureChar)
    if (char == ';') return Token(SEMI , captureChar)
    if (char == ',') return Token(COMMA, captureChar)

    if (char == '+') return Token(PLUS , captureChar)
    if (char == '-') return Token(MINUS, captureChar)
    if (char == '*') return Token(STAR , captureChar)
    if (char == '/') {
      if (isAhead('/')) {
        mark()
        skip(c => c != '\n')
        return Token(COMMENT, marked)
      }
      return Token(SLASH, captureChar)
    }
    if (char == '%') return Token(PERCENT , captureChar)
    if (char == '!') return Token(BANG    , captureChar)
    if (char == '<') {
      if (isAhead('=')) {
        return Token(LT_EQ, captureChars(2))
      }
      return Token(LT, captureChar)
    }
    if (char == '>')  {
      if (isAhead('=')) {
        return Token(GT_EQ, captureChars(2))
      }
      return Token(GT, captureChar)
    }

    if (char == '&' && isAhead('&')) return Token(AMP_AMP, captureChars(2))
    if (char == '|' && isAhead('|')) return Token(BAR_BAR, captureChars(2))

    if (char == '(') return Token(LPAREN, captureChar)
    if (char == ')') return Token(RPAREN, captureChar)

    if (char == '{') return Token(LBRACE, captureChar)
    if (char == '}') return Token(RBRACE, captureChar)

    if (char.isWhitespace)  return Token(WS             , capture(_.isWhitespace))
    if (char.isDigit)       return Token(NUMBER_LITERAL , capture(_.isDigit))

    if (char == '"') {
      mark()
      advance()
      if (!hasNext) return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      skip(c => c != '"' && c != '\n')
      if (!hasNext || char != '"') return Token(STRING_LITERAL, marked, Some("Unclosed string"))
      advance()
      return Token(STRING_LITERAL, marked)
    }

    if (char.isLetter) {
      val span = capture(_.isLetterOrDigit)
      val text = span.text

      if (text == "var")      return Token(VAR, span)
      if (text == "def")      return Token(DEF, span)
      if (text == "while")    return Token(WHILE, span)
      if (text == "if")       return Token(IF, span)
      if (text == "else")     return Token(ELSE, span)
      if (text == "return")   return Token(RETURN, span)

      if (text == "boolean")  return Token(BOOLEAN, span)
      if (text == "string")   return Token(STRING, span)
      if (text == "integer")  return Token(INTEGER, span)
      if (text == "void")     return Token(VOID, span)

      if (text == "true" || text == "false") return Token(BOOLEAN_LITERAL, span)

      return Token(IDENT, span)
    }

    Token(UNKNOWN, captureChar, Some("Unknown token"))
  }
}


