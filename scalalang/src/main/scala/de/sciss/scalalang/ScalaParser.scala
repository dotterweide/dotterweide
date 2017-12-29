package de.sciss.scalalang

import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}
import de.sciss.scalalang.node.PackageNode

object ScalaParser extends Parser {
  def parse(in: TreeBuilder): Unit = {
    in.capturing(new PackageNode()) {
      in.consume()
      while (!in.isEOF) () // StatementParser.parse(in)
    }
  }
}
