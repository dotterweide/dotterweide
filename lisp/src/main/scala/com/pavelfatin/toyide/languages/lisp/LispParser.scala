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

import com.pavelfatin.toyide.languages.lisp.LispTokens._
import com.pavelfatin.toyide.languages.lisp.node._
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

object LispParser extends Parser {
  def parse(in: TreeBuilder): Unit =
    in.capturing(new ProgramNode()) {
      while (!in.isEOF) expression(in)
    }

  def expression(in: TreeBuilder): Unit =
    if (in.matches(COMMENT)) {
      comment(in)
    } else if (matches(in, LPAREN)) {
      list(in, LPAREN, RPAREN)
    } else if (matches(in, LBRACKET)) {
      list(in, LBRACKET, RBRACKET)
    } else if (matches(in, INTEGER_LITERAL)) {
      integer(in)
    } else if (matches(in, BOOLEAN_LITERAL)) {
      boolean(in)
    } else if (matches(in, CHARACTER_LITERAL)) {
      character(in)
    } else if (matches(in, STRING_LITERAL)) {
      string(in)
    } else if (matches(in, PREDEFINED_SYMBOL, CUSTOM_SYMBOL)) {
      symbol(in)
    } else {
      in.error("Expression expected")
      if (!in.isEOF) in.advance()
    }

  def comment(in: TreeBuilder): Unit =
    in.capturing(new CommentNode()) {
      in.consume(COMMENT)
    }

  def list(in: TreeBuilder, leftBound: TokenKind, rightBound: TokenKind): Unit =
    in.capturing(new ListNode()) {
      in.grasp(Prefixes: _*)
      in.consume(leftBound)
      while (!in.matches(rightBound) && !in.isEOF) {
        if (in.matches(COMMA)) in.consume()
        expression(in)
      }
      in.consume(rightBound)
    }

  def integer(in: TreeBuilder): Unit =
    in.capturing(new IntegerLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(INTEGER_LITERAL)
    }

  def boolean(in: TreeBuilder): Unit =
    in.capturing(new BooleanLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(BOOLEAN_LITERAL)
    }

  def character(in: TreeBuilder): Unit =
    in.capturing(new CharacterLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(CHARACTER_LITERAL)
    }

  def string(in: TreeBuilder): Unit =
    in.capturing(new StringLiteralNode()) {
      in.grasp(Prefixes: _*)
      in.consume(STRING_LITERAL)
    }

  def symbol(in: TreeBuilder): Unit =
    in.capturing(new SymbolNode()) {
      in.grasp(Prefixes: _*)
      in.consume(CUSTOM_SYMBOL, PREDEFINED_SYMBOL)
    }

  private def matches(in: TreeBuilder, kinds: TokenKind*): Boolean =
    in.matches(Prefixes: _*) && in.ahead(kinds: _*) || in.matches(kinds: _*)
}