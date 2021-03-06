/*
 *  TokenKind.scala
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

package dotterweide.lexer

/** @param name   the token name or identifier
  * @param data   if `true`, the token's source text should be used in
  *               `toString` representations, if `false`, the token name should be used
  */
case class TokenKind(name: String, data: Boolean = false)

object TokenKind {
  val WS      = TokenKind("WS"      , data = true)
  val UNKNOWN = TokenKind("UNKNOWN" , data = true)
}
