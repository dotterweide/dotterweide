/*
 *  CallParser.scala
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
import dotterweide.languages.toy.node.Call
import dotterweide.parser.{SyncParser, TreeBuilder}

object CallParser extends SyncParser {
  def parseTo(in: TreeBuilder): Unit =
    in.capturing(new Call()) {
      CallExpressionParser.parseTo(in)
      in.consume(SEMI)
    }
}