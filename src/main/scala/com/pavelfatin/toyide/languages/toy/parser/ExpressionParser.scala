/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.toy.parser

import com.pavelfatin.toyide.parser.{TreeBuilder, Parser}
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.node._

object ExpressionParser extends Parser {
  def parse(in: TreeBuilder): Unit = {
    logicalOr()

    def logicalOr(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        logicalAnd()
        while (in.grasp(BAR_BAR)) logicalAnd()
      }
    }

    def logicalAnd(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        equality()
        while (in.grasp(AMP_AMP)) equality()
      }
    }

    def equality(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        relation()
        while (in.grasp(EQ_EQ, BANG_EQ)) relation()
      }
    }

    def relation(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        addition()
        while (in.grasp(LT, LT_EQ, GT_EQ, GT)) addition()
      }
    }

    def addition(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        multiplication()
        while (in.grasp(PLUS, MINUS)) multiplication()
      }
    }

    def multiplication(): Unit = {
      in.folding(new BinaryExpression(), collapseHolderNode = true) {
        prefix()
        while (in.grasp(STAR, SLASH, PERCENT)) prefix()
      }
    }

    def prefix(): Unit = {
      if (in.matches(PLUS, MINUS, BANG)) {
        in.capturing(new PrefixExpression()) {
          in.consume()
          prefix()
        }
      } else {
        atom()
      }
    }

    def atom(): Unit = {
      if (in.matches(LPAREN)) {
        in.capturing(new Group()) {
          in.consume()
          logicalOr()
          in.consume(RPAREN)
        }
      } else {
        if(in.matches(IDENT)) {
          if(in.ahead(LPAREN))
            CallExpressionParser.parse(in)
          else in.capturing(new ReferenceToValue()) {
            in.consume(IDENT)
          }
        } else {
          if(in.matches(NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL)) {
            in.capturing(new Literal()) {
              in.consume()
            }
          } else {
            in.error("Expression expected")
          }
        }
      }
    }
  }
}