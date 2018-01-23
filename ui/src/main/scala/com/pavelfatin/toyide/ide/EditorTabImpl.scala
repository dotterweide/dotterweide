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