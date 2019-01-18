/*
 *  QuotableNode.scala
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
