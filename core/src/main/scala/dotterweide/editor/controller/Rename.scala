/*
 *  Rename.scala
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
import dotterweide.editor.{Action, History, Data, Terminal}

private class Rename(document: Document, terminal: Terminal, data: Data, history: History) extends Action {
  def keys: Seq[String] = List("shift pressed F6")

  def apply(): Unit = {
    data.compute()
    val leafs = data.connectedLeafsFor(terminal.offset)
    if (leafs.nonEmpty) {
      terminal.selection  = None
      terminal.highlights = leafs.map(_.span.interval)
      val id = leafs.head.span.text
      terminal.edit(id, "Rename") {
        case Some(text) =>
          terminal.highlights = Nil
          history.recording(document, terminal) {
            val anchor = document.createAnchorAt(terminal.offset, Bias.Right)
            leafs.map(_.span.interval).sortBy(_.begin).reverse.foreach(document.replace(_, text))
            terminal.offset = anchor.offset
            anchor.dispose()
          }
        case None =>
          terminal.highlights = Nil
      }
    }
  }
}