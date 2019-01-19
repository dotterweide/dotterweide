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

package dotterweide.languages.toy.parser

import dotterweide.languages.toy.ToyTokens._
import dotterweide.parser.{SyncParser, TreeBuilder}

object BlockStatementParser extends SyncParser {
  def parseTo(in: TreeBuilder): Unit = {
    if (in.matches(VAR)) {
      VariableParser.parseTo(in)
      return
    }

    if (in.matches(RETURN)) {
      ReturnParser.parseTo(in)
      return
    }

    if (in.matches(WHILE)) {
      WhileParser.parseTo(in)
      return
    }

    if (in.matches(IF)) {
      IfParser.parseTo(in)
      return
    }

    if (in.matches(IDENT)) {
      if (in.ahead(LPAREN)) {
        CallParser.parseTo(in)
        return
      }

      if (in.ahead(EQ)) {
        AssignmentParser.parseTo(in)
        return
      }
    }

    if (in.matches(COMMENT)) {
      CommentParser.parseTo(in)
      return
    }

    if (in.matches(SEMI)) {
      EmptyParser.parseTo(in)
      return
    }

    in.error("Wrong statement")

    if (!in.isEOF) in.advance()
  }
}