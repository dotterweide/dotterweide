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
import dotterweide.editor.{Async, Data, StructureAction, Terminal}
import dotterweide.formatter.Formatter
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

private class Format(document: Document, terminal: Terminal, val data: Data, formatter: Formatter, tabSize: Int)
                    (implicit val async: Async) extends StructureAction {
  def name: String        = "Reformat"
  def mnemonic: Char      = 'F'
  def keys: ISeq[String]  = "ctrl alt pressed L" :: Nil

  def applyWithStructure(root: Node): Unit = {
    val text        = formatter.format(root, terminal.selection, tabSize)
    terminal.offset = terminal.offset.min(text.length)
    document.text   = text
  }
}