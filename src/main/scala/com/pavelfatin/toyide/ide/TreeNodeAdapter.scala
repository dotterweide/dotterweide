/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.ide

import java.util
import javax.swing.tree.TreeNode

import com.pavelfatin.toyide.node.Node

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