/*
 *  FunctionParser.scala
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
import com.pavelfatin.toyide.languages.toy.node.FunctionDeclaration
import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}

object FunctionParser extends Parser {
  def parse(in: TreeBuilder): Unit =
    in.capturing(new FunctionDeclaration()) {
      in.consume(DEF)
      in.consume(IDENT)
      ParametersParser.parse(in)
      TypeSpecParser.parse(in)
      in.consume(EQ)
      BlockParser.parse(in)
    }
}