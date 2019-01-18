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