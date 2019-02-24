/*
 *  StructureTab.scala
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

import java.awt.event.{FocusAdapter, FocusEvent}

import dotterweide.editor.{Data, DataEvent, Pass, Terminal}
import javax.swing.JTree
import javax.swing.event.{TreeSelectionEvent, TreeSelectionListener}
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeModel}

import scala.swing.{BorderPanel, Component}

private class StructureTab(data: Data, terminal: Terminal) extends BorderPanel {
  private val tree = new JTree(new DefaultMutableTreeNode("root"))

  tree.setEditable(false)

  add(Component.wrap(tree), BorderPanel.Position.Center)

  private val dataL: DataEvent => Unit = {
    case DataEvent(Pass.Parser, _) =>
      val root = data.structure.getOrElse(
        throw new IllegalStateException("No root node after parser pass"))

      tree.setModel(new DefaultTreeModel(TreeNodeAdapter(root)))
    case _ =>
  }

  data.onChange(dataL)

  private val treeSelectionL = new TreeSelectionListener() {
    def valueChanged(e: TreeSelectionEvent): Unit =
      updateTreeHighlight()
  }

  tree.addTreeSelectionListener(treeSelectionL)

  private val treeFocusL = new FocusAdapter() {
    override def focusGained(e: FocusEvent): Unit = {
      terminal.selection = None
      updateTreeHighlight()
    }

    override def focusLost(e: FocusEvent): Unit = {
      terminal.highlights = Nil
      tree.clearSelection()
    }
  }

  tree.addFocusListener(treeFocusL)

  private def updateTreeHighlight(): Unit = {
    val selection = Option(tree.getSelectionPath).map(_.getLastPathComponent.asInstanceOf[TreeNodeAdapter])
    selection.map(_.delegate).foreach { node =>
      terminal.highlights = node.span.interval :: Nil
    }
  }

  def dispose(): Unit = {
    data.disconnect(dataL)
    tree.removeTreeSelectionListener(treeSelectionL)
    tree.removeFocusListener(treeFocusL)
  }
}