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

import java.io.File

import com.pavelfatin.toyide.FileType
import com.pavelfatin.toyide.editor.{Editor, History}

import scala.swing.{BorderPanel, Orientation, ScrollPane, SplitPane}

private class EditorTabImpl(val fileType: FileType, val history: History,
                            primaryEditor: Editor, secondaryEditor: => Editor) extends BorderPanel with EditorTab {

  private val structure = new StructureTab(primaryEditor.data, primaryEditor.terminal)
  private var _split    = false
  private var _original = ""
  private var _file     = Option.empty[File]

  updateLayout()

  def text: String = primaryEditor.text

  def text_=(s: String): Unit = {
    primaryEditor.text = s
    _original = s
    history.clear()
  }

  def file: Option[File] = _file

  def file_=(file: Option[File]): Unit = {
    _file = file
    notifyObservers()
  }

  def changed: Boolean = text != _original

  def split: Boolean = _split

  def split_=(b: Boolean): Unit = {
    _split = b
    updateLayout()
    val editor = if (split) secondaryEditor else primaryEditor
    editor.pane.requestFocusInWindow()
  }

  private def updateLayout(): Unit = {
    val editors = if (split) {
      val pane = new SplitPane(Orientation.Horizontal, primaryEditor.component, secondaryEditor.component)
      pane.resizeWeight = 0.5D
      pane.border = null
      pane
    } else {
      primaryEditor.component
    }

    val pane = new SplitPane(Orientation.Vertical, editors, new ScrollPane(structure))
    pane.resizeWeight = 0.7D

    peer.removeAll()
    add(pane, BorderPanel.Position.Center)
    revalidate()
  }
}