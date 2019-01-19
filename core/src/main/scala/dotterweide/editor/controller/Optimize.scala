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
import dotterweide.editor.{Action, Data, Terminal}
import dotterweide.optimizer.Optimizer

import scala.collection.immutable.{Seq => ISeq}

private class Optimize(document: Document, terminal: Terminal, data: Data) extends Action {
  def name: String        = "Optimize"
  def mnemonic: Char      = 'O'
  def keys: ISeq[String]  = "ctrl alt pressed O" :: Nil

  def apply(): Unit = {
    data.compute()
    data.structure.foreach { root =>
      terminal.highlights = Nil
      terminal.selection  = None

      val anchor = document.createAnchorAt(terminal.offset, Bias.Left)
      terminal.offset = 0

      Optimizer.optimize(root, document)

      terminal.offset = anchor.offset
      anchor.dispose()
    }
  }
}