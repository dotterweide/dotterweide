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

package com.pavelfatin.toyide.languages.toy.parser

import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

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
        if (in.matches(IDENT)) {
          if (in.ahead(LPAREN))
            CallExpressionParser.parse(in)
          else in.capturing(new ReferenceToValue()) {
            in.consume(IDENT)
          }
        } else {
          if (in.matches(NUMBER_LITERAL, STRING_LITERAL, BOOLEAN_LITERAL)) {
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