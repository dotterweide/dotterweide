package de.sciss.scalalang

import com.pavelfatin.toyide.editor.{Adviser, Variant}
import com.pavelfatin.toyide.node.Node

object ScalaAdviser extends Adviser {
  def variants(root: Node, anchor: Node): Seq[Variant] = Nil
}