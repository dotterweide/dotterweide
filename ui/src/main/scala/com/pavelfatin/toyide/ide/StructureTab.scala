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