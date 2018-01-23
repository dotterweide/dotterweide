/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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