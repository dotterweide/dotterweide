package de.sciss.scalalang

import com.pavelfatin.toyide.parser.{Parser, TreeBuilder}
import de.sciss.scalalang.node.Program

object ScalaParser extends Parser {
  def parse(in: TreeBuilder): Unit = in.capturing(new Program()) {
    // while (!in.isEOF) StatementParser.parse(in)
  }
}
