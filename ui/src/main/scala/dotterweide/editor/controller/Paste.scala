/*
 *  Paste.scala
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
import java.awt.datatransfer.DataFlavor

import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}

private class Paste(document: Document, terminal: Terminal) extends Action {
  def name: String        = "Paste"
  def mnemonic: Char      = 'P'
  def keys: List[String]  = List("ctrl pressed V", "shift pressed INSERT")

  def apply(): Unit = {
    val contents = Toolkit.getDefaultToolkit.getSystemClipboard.getContents(null)
    if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
      val text = contents.getTransferData(DataFlavor.stringFlavor).asInstanceOf[String]
      terminal.insertInto(document, text)
    }
  }
}