/*
 *  MockLexer.scala
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

package dotterweide

import dotterweide.lexer.{Token, TokenKind, Lexer}

object MockLexer extends Lexer {
  private val TokenPattern = """(\p{Lu}|[(){}])\p{Ll}*""".r

  def analyze(input: CharSequence): Iterator[Token] = {
    TokenPattern.findAllIn(input).matchData.map { m =>
      Token(MockTokenKind, Span(input, m.start, m.end))
    }
  }
}

object MockTokenKind extends TokenKind("token", true)