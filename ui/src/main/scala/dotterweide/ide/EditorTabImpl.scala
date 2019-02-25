/*
 *  EditorTabImpl.scala
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

import java.io.File

import dotterweide.FileType
import dotterweide.editor.{Editor, History}

import scala.swing.{BorderPanel, Component, Orientation, ScrollPane, SplitPane}

private class EditorTabImpl(val fileType: FileType, val history: History,
                            primaryEditor: Editor, secondaryEditor: => Editor) extends BorderPanel with EditorTab {

  private[this] var _split    = false
  private[this] var _file     = Option.empty[File]
  private[this] var structure = Option.empty[StructureTab]

  updateLayout()

  def component: Component = this

  def text: String = primaryEditor.text

  def text_=(s: String): Unit = {
    primaryEditor.text = s
    history.clear()
  }

  def file: Option[File] = _file

  def file_=(file: Option[File]): Unit = if (_file != file) {
    _file = file
    notifyObservers(EditorTab.FileChanged(file))
  }

  private[this] var _dirty = history.canUndo

  history.onChange {
    val newDirty = history.canUndo
    if (_dirty != newDirty) {
      _dirty = newDirty
      notifyObservers(EditorTab.DirtyChanged(newDirty))
    }
  }

  def isDirty: Boolean = _dirty

  def structureVisible: Boolean = structure.isDefined

  def structureVisible_=(value: Boolean): Unit = if (value != structure.isDefined) {
    structure.foreach(_.dispose())
    structure = if (!value) None else {
      val st = new StructureTab(primaryEditor.data, primaryEditor.terminal)
      Some(st)
    }
    updateLayout()
  }

  def split: Boolean = _split

  def split_=(b: Boolean): Unit = if (_split != b) {
    _split = b
    updateLayout()
    val editor = if (split) secondaryEditor else primaryEditor
    editor.pane.requestFocusInWindow()
    notifyObservers(EditorTab.SplitChanged(b))
  }

  private def updateLayout(): Unit = {
    val editors = if (split) {
      val pane = new SplitPane(Orientation.Horizontal, primaryEditor.component, secondaryEditor.component)
      pane.resizeWeight = 0.5
      pane.border = null
      pane
    } else {
      primaryEditor.component
    }

    val pane = structure match {
      case Some(st) =>
        val res = new SplitPane(Orientation.Vertical, editors, new ScrollPane(st))
        res.resizeWeight = 0.7
        res
      case None =>
        editors
    }

    peer.removeAll()
    add(pane, BorderPanel.Position.Center)
    revalidate()
  }
}