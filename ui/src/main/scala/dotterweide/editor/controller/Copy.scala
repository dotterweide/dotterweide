/*
 *  Copy.scala
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

package dotterweide.editor.controller

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}

private class Copy(document: Document, terminal: Terminal) extends Action {
  def keys: Seq[String] = List("ctrl pressed C", "ctrl pressed INSERT")

  def apply(): Unit = {
    if (terminal.selection.isEmpty)
      terminal.selection = Some(terminal.currentLineIntervalIn(document))

    terminal.selection.foreach { s =>
      val text = new StringSelection(document.text(s))
      Toolkit.getDefaultToolkit.getSystemClipboard.setContents(text, null)
    }
  }
}