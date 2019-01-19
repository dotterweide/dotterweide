/*
 *  ExpressionParser.scala
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

package dotterweide.languages.toy.parser

import dotterweide.languages.toy.ToyTokens._
import dotterweide.languages.toy.node._
import dotterweide.parser.{SyncParser, TreeBuilder}

object ExpressionParser extends SyncParser {
  def parseTo(in: TreeBuilder): Unit = {
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
            CallExpressionParser.parseTo(in)
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