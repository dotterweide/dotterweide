/*
 *  ShowUsages.scala
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

import dotterweide.editor.{Async, Data, StructureAction, Terminal}
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

private class ShowUsages(terminal: Terminal, val data: Data)(implicit val async: Async) extends StructureAction {
  def name: String        = "Show Usages"
  def mnemonic: Char      = 'S'
  def keys: ISeq[String]  = "shift ctrl pressed F7" :: Nil

  def applyWithStructure(root: Node): Unit = {
    terminal.highlights = data.connectedLeafsFor(terminal.offset).map(_.span.interval)
  }
}