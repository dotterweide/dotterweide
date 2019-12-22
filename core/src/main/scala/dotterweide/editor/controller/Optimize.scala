/*
 *  Optimize.scala
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

import dotterweide.document.{Bias, Document}
import dotterweide.editor.{Action, Async, Data, StructureAction, Terminal}
import dotterweide.node.Node
import dotterweide.optimizer.Optimizer

import scala.collection.immutable.{Seq => ISeq}

private class Optimize(document: Document, terminal: Terminal, val data: Data)(implicit val async: Async)
  extends Action with StructureAction {

  def name: String        = "Optimize"
  def mnemonic: Char      = 'O'
  val keys: ISeq[String]  = "ctrl alt pressed O" :: Nil

  def applyWithStructure(root: Node): Unit = {
    terminal.highlights = Nil
    terminal.selection  = None

    // the anchor allows us to adjust the cursor position
    val anchor = document.createAnchorAt(terminal.offset, Bias.Left)
    terminal.offset = 0

    Optimizer.optimize(root, document)

    terminal.offset = anchor.offset
    anchor.dispose()
  }
}