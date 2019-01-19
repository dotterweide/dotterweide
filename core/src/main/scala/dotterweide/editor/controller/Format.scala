/*
 *  Format.scala
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
import dotterweide.editor.{Action, Data, Terminal}
import dotterweide.formatter.Formatter

import scala.collection.immutable.{Seq => ISeq}

private class Format(document: Document, terminal: Terminal, data: Data, formatter: Formatter, tabSize: Int) extends Action {
  def name: String        = "Reformat"
  def mnemonic: Char      = 'F'
  def keys: ISeq[String]  = "ctrl alt pressed L" :: Nil

  def apply(): Unit = {
    ??? // data.compute()
    data.structure.foreach { root =>
      val text        = formatter.format(root, terminal.selection, tabSize)
      terminal.offset = terminal.offset.min(text.length)
      document.text   = text
    }
  }
}