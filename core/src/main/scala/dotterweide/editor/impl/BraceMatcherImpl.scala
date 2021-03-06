/*
 *  BraceMatcherImpl.scala
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

package dotterweide.editor.impl

import dotterweide.editor.{BraceMatcher, BraceType, Inapplicable, Paired, Unbalanced}
import dotterweide.lexer.{Token, TokenKind}

import scala.collection.immutable.{Seq => ISeq}

class BraceMatcherImpl(complements: ISeq[(TokenKind, TokenKind)]) extends BraceMatcher {
  def braceTypeOf(token: Token, tokens: ISeq[Token], offset: Int): BraceType = {
    def right(complement: (TokenKind, TokenKind)): Option[BraceType] = {
      if (token.kind != complement._1) return None

      val tail = tokens.dropWhile(!_.eq(token)).tail

      complementIn(tail, complement._1, complement._2).map { it =>
        if (token.span.start == offset || it.span.stop == offset) Paired else Inapplicable
      } orElse Some {
        if (token.span.start == offset) Unbalanced else Inapplicable
      }
    }

    def left(complement: (TokenKind, TokenKind)): Option[BraceType] = {
      if (token.kind != complement._2) return None

      val tail = tokens.takeWhile(!_.eq(token)).reverse

      complementIn(tail, complement._2, complement._1).map { it =>
        if (token.span.stop == offset || it.span.start == offset) Paired else Inapplicable
      } orElse Some {
        if (token.span.stop == offset) Unbalanced else Inapplicable
      }
    }

    val variants = complements.view.flatMap(right) ++ complements.view.flatMap(left)

    variants.headOption.getOrElse(Inapplicable)
  }

  def complementIn(tail: ISeq[Token], opening: TokenKind, closing: TokenKind): Option[Token] = {
    var level = 0
    tail.foreach { it =>
      if (it.kind == opening) level += 1
      if (it.kind == closing) {
        if (level == 0) return Some(it)
        level -= 1
      }
    }
    None
  }
}