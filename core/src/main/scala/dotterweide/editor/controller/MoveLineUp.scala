/*
 *  MoveLineUp.scala
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

import dotterweide.document.Document
import dotterweide.editor.Terminal

private class MoveLineUp(document: Document, terminal: Terminal) extends DocumentAction(document, terminal) {
  def name: String        = "Move Line Up"
  def mnemonic: Char      = 'U'
  def keys: List[String]  = "shift ctrl pressed UP" :: Nil

  protected def calcEnabled(): Boolean = document.lineNumberOf(terminal.offset) > 0


  def apply(): Unit = {
    val location = document.toLocation(terminal.offset)

    val source = document.intervalOf(location.line)
    val target = document.intervalOf(location.line - 1)

    val sourceText = document.text(source)

    terminal.selection = None

    document.replace(source, document.text(target))
    document.replace(target, sourceText)

    document.toOffset(location.copy(line = location.line - 1)).foreach(terminal.offset = _)
  }
}