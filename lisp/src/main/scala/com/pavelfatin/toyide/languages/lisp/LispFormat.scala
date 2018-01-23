/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.formatter.Distance._
import com.pavelfatin.toyide.formatter.{Distance, Format}
import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.lexer.TokenKind

object LispFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind): Distance = (a, b) match {
    case (LPAREN | LBRACKET, _)                           => Joint
    case (_, RPAREN | RBRACKET)                           => Joint
    case (RPAREN | RBRACKET, LPAREN | LBRACKET)           => Lines
    case (QUOTE | TILDE | TILDE_AT | BACKQUOTE | HASH, _) => Joint
    case (COMMENT, _)                                     => Lines
    case _                                                => Space
  }

  def indentDeltaFor(a: TokenKind, b: TokenKind) = 0
}