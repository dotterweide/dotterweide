package de.sciss.scalalang

import dotterweide.parser.{Parser, TreeBuilder}
import de.sciss.scalalang.node.PackageNode

// XXX TODO
object ScalaParser extends Parser {
  def parse(in: TreeBuilder): Unit = {
    in.capturing(new PackageNode()) {
      in.consume()
      while (!in.isEOF) in.consume()  // StatementParser.parse(in)
    }
  }
}
