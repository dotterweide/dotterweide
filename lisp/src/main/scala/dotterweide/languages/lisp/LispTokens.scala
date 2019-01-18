/*
 *  LispTokens.scala
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

import dotterweide.lexer.TokenKind

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