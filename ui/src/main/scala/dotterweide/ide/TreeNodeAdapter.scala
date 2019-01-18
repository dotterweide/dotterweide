/*
 *  TreeNodeAdapter.scala
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

package dotterweide.ide

import java.util
import javax.swing.tree.TreeNode

import dotterweide.node.Node

import scala.collection.JavaConverters._

private case class TreeNodeAdapter(delegate: Node) extends TreeNode {
  private def convert(node: Node) = TreeNodeAdapter(node)

  def children: util.Enumeration[TreeNodeAdapter] = delegate.children.map(convert).iterator.asJavaEnumeration

  def isLeaf: Boolean = delegate.isLeaf

  def getAllowsChildren: Boolean = !isLeaf

  def getIndex(node: TreeNode): Int =
    delegate.children.indexWhere(node.asInstanceOf[TreeNodeAdapter].delegate == _)

  def getParent: TreeNodeAdapter = delegate.parent.map(convert).orNull

  def getChildCount: Int = delegate.children.size

  def getChildAt(childIndex: Int): TreeNodeAdapter = convert(delegate.children(childIndex))

  override def toString: String = delegate.toString
}