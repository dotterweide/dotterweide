/*
 *  ScalaParser.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import dotterweide.languages.scala.node.PackageNode
import dotterweide.parser.{Parser, TreeBuilder}

// XXX TODO
object ScalaParser extends Parser {
  def parse(in: TreeBuilder): Unit = {
    in.capturing(new PackageNode()) {
      in.consume()
      while (!in.isEOF) in.consume()  // StatementParser.parse(in)
    }
  }
}
