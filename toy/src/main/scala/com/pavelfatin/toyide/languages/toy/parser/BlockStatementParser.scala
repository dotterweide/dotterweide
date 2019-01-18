/*
 *  BlockStatementParser.scala
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

package com.pavelfatin.toyide.languages.toy.parser

import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

object BlockStatementParser extends Parser {
  def parse(in: TreeBuilder): Unit = {
    if (in.matches(VAR)) {
      VariableParser.parse(in)
      return
    }

    if (in.matches(RETURN)) {
      ReturnParser.parse(in)
      return
    }

    if (in.matches(WHILE)) {
      WhileParser.parse(in)
      return
    }

    if (in.matches(IF)) {
      IfParser.parse(in)
      return
    }

    if (in.matches(IDENT)) {
      if (in.ahead(LPAREN)) {
        CallParser.parse(in)
        return
      }

      if (in.ahead(EQ)) {
        AssignmentParser.parse(in)
        return
      }
    }

    if (in.matches(COMMENT)) {
      CommentParser.parse(in)
      return
    }

    if (in.matches(SEMI)) {
      EmptyParser.parse(in)
      return
    }

    in.error("Wrong statement")

    if (!in.isEOF) in.advance()
  }
}