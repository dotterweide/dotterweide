/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.lexer.TokenKind

object LispTokens {
  val COMMENT           = TokenKind("COMMENT", data = true)

  val COMMA             = TokenKind("COMMA")

  val HASH              = TokenKind("HASH")

  val QUOTE             = TokenKind("QUOTE")
  val TILDE             = TokenKind("TILDE")
  val TILDE_AT          = TokenKind("TILDE_AT")
  val BACKQUOTE         = TokenKind("BACKQUOTE")

  val Prefixes: Seq[TokenKind] = Seq(QUOTE, TILDE, TILDE_AT, BACKQUOTE, HASH)

  val LPAREN            = TokenKind("LPAREN")
  val RPAREN            = TokenKind("RPAREN")

  val Parens: (TokenKind, TokenKind) = (LPAREN, RPAREN)

  val LBRACKET          = TokenKind("LBRACKET")
  val RBRACKET          = TokenKind("RBRACKET")

  val Brackets: (TokenKind, TokenKind) = (LBRACKET, RBRACKET)

  val INTEGER_LITERAL   = TokenKind("INTEGER_LITERAL", data = true)
  val BOOLEAN_LITERAL   = TokenKind("BOOLEAN_LITERAL", data = true)
  val STRING_LITERAL    = TokenKind("STRING_LITERAL", data = true)
  val CHARACTER_LITERAL = TokenKind("CHARACTER_LITERAL", data = true)

  val Literals: Seq[TokenKind] = Seq(INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, CHARACTER_LITERAL)

  val PREDEFINED_SYMBOL = TokenKind("PREDEFINED_SYMBOL", data = true)
  val CUSTOM_SYMBOL     = TokenKind("CUSTOM_SYMBOL", data = true)

  val Symbols: Seq[TokenKind] = Seq(PREDEFINED_SYMBOL, CUSTOM_SYMBOL)
}