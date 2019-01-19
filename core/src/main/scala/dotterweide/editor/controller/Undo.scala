/*
 *  Undo.scala
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

import scala.collection.immutable.{Seq => ISeq}

// XXX TODO --- should observe `history` instead
private class Undo(document: Document, terminal: Terminal, history: History)
  extends DocumentAction(document, terminal) {

  def name: String        = "Undo"
  def mnemonic: Char      = 'U'
  def keys: ISeq[String]  = "ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canUndo

  def apply(): Unit =
    history.undo()
}