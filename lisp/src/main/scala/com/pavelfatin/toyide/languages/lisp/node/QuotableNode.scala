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

package com.pavelfatin.toyide.languages.lisp.node

import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.languages.lisp.core.{Quasiquote, Quote, Unquote, UnquoteSplicing}
import com.pavelfatin.toyide.languages.lisp.value.Expression
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.node.Node

trait QuotableNode { self: Node with ReadableNode =>
  protected def prefixKind: Option[TokenKind] =
    children.headOption.flatMap(_.token).map(_.kind).filter(Prefixes.contains)

  def quoted: Boolean = prefixKind.isDefined

  final def read(source: String): Expression = {
    val value = read0(source)

    prefixKind collect {
      case QUOTE      => Quote(value)
      case TILDE      => Unquote(value)
      case TILDE_AT   => UnquoteSplicing(value)
      case BACKQUOTE  => Quasiquote(value)
    } getOrElse {
      value
    }
  }

  protected def read0(source: String): Expression

  protected def text: String = if (quoted) span.text.substring(children.head.span.length) else span.text
}
