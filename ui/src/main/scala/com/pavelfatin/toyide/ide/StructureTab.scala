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

import java.awt.event.{FocusAdapter, FocusEvent}
import javax.swing.JTree
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.tree.DefaultTreeModel

import com.pavelfatin.toyide.editor.{Data, DataEvent, Pass, Terminal}

import scala.swing.{BorderPanel, Component}

private class StructureTab(data: Data, terminal: Terminal) extends BorderPanel {
  private val tree = new JTree()

  tree.setEditable(false)

  add(Component.wrap(tree), BorderPanel.Position.Center)

  data.onChange {
    case DataEvent(Pass.Parser, _) =>
      val root = data.structure.getOrElse(
        throw new IllegalStateException("No root node after parser pass"))

      tree.setModel(new DefaultTreeModel(TreeNodeAdapter(root)))
    case _ =>
  }

  tree.addTreeSelectionListener(new TreeSelectionListener() {
    def valueChanged(e: TreeSelectionEvent): Unit =
      updateTreeHighlight()
  })

  tree.addFocusListener(new FocusAdapter() {
    override def focusGained(e: FocusEvent): Unit = {
      terminal.selection = None
      updateTreeHighlight()
    }

    override def focusLost(e: FocusEvent): Unit = {
      terminal.highlights = Nil
      tree.clearSelection()
    }
  })

  private def updateTreeHighlight(): Unit = {
    val selection = Option(tree.getSelectionPath).map(_.getLastPathComponent.asInstanceOf[TreeNodeAdapter])
    selection.map(_.delegate).foreach { node =>
      terminal.highlights = node.span.interval :: Nil
    }
  }
}