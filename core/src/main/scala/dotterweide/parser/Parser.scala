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

package dotterweide.parser

import dotterweide.editor.Async
import dotterweide.lexer.Token
import dotterweide.node.Node

import scala.concurrent.Future

trait Parser {
  def parseAsync(text: String, tokens: Iterator[Token])(implicit async: Async): Future[Node]
}

trait SyncParser extends Parser {
  def parseAsync(text: String, tokens: Iterator[Token])(implicit async: Async): Future[Node] =
    Future.successful {
      parse(tokens)
    }

  def parse(tokens: Iterator[Token]): Node = {
    val builder = new TreeBuilder(tokens)
    parseTo(builder)
    builder.tree
  }

  protected def parseTo(in: TreeBuilder): Unit
}
