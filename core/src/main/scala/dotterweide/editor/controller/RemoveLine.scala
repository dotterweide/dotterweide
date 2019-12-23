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

import dotterweide.Platform
import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}

import scala.collection.immutable.{Seq => ISeq}

private class RemoveLine(document: Document, terminal: Terminal)(implicit p: Platform) extends Action {
  def name: String        = "Remove Line"
  def mnemonic: Char      = 'R'
  val keys: ISeq[String]  =
    (if (p.isMac) "meta pressed BACK_SPACE" else "ctrl pressed Y") :: Nil

  def apply(): Unit = {
    val oldPos    = terminal.offset
    val line      = document.lineNumberOf(oldPos)
    val range     = document.intervalOfNl(line)
    val numLines  = document.linesCount - 1 // after removal
    val newPos    = if (line == numLines) range.start else {
      val nextRange = document.intervalOf(line + 1)
      val lineOff   = math.min(nextRange.length, oldPos - range.start)
      range.start + lineOff
    }

    terminal.selection = None
    terminal.offset = newPos
    document.remove(range)
  }
}