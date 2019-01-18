/*
 *  Redo.scala
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
import dotterweide.editor.{History, Terminal}

// XXX TODO --- should observe `history` instead
private class Redo(document: Document, terminal: Terminal, history: History)
  extends DocumentAction(document, terminal) {

  def name: String        = "Redo"
  def mnemonic: Char      = 'R'
  def keys: List[String]  = "shift ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canRedo

  def apply(): Unit =
    history.redo()
}