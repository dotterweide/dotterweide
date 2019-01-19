/*
 *  RemoveLine.scala
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
import dotterweide.editor.{Action, Terminal}

import scala.collection.immutable.{Seq => ISeq}

private class RemoveLine(document: Document, terminal: Terminal) extends Action {
  def name: String        = "Remove Line"
  def mnemonic: Char      = 'R'
  def keys: ISeq[String]  = "ctrl pressed Y" :: Nil

  def apply(): Unit = {
    val range = terminal.currentLineIntervalIn(document)
    terminal.selection = None
    terminal.offset = terminal.offset.min(document.length - range.length)
    document.remove(range)
  }
}