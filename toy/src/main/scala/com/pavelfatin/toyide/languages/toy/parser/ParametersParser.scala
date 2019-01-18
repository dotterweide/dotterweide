/*
 *  ParametersParser.scala
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
import com.pavelfatin.toyide.languages.toy.node.Parameters
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

object ParametersParser extends Parser {
  def parse(in: TreeBuilder): Unit =
    in.capturing(new Parameters()) {
      in.consume(LPAREN)
      if (!in.isEOF && !in.matches(RPAREN)) {
        ParameterParser.parse(in)
        while (in.grasp(COMMA)) ParameterParser.parse(in)
      }
      in.consume(RPAREN)
    }
}