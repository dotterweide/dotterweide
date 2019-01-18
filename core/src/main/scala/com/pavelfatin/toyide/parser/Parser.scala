/*
 *  Parser.scala
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

package com.pavelfatin.toyide.parser

import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.node.Node

trait Parser {
  def parse(tokens: Iterator[Token]): Node = {
    val builder = new TreeBuilder(tokens)
    parse(builder)
    builder.tree
  }

  protected def parse(in: TreeBuilder): Unit
}
