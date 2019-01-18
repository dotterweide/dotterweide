package de.sciss.scalalang

import dotterweide.editor.{Adviser, Variant}
import dotterweide.node.Node

object ScalaAdviser extends Adviser {
  def variants(root: Node, anchor: Node): Seq[Variant] = Nil
}