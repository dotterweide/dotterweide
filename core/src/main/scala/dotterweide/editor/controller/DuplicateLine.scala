/*
 *  DuplicateLine.scala
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

import dotterweide.{Interval, Platform}
import dotterweide.document.Document
import dotterweide.editor.{Action, Terminal}

import scala.collection.immutable.{Seq => ISeq}

private class DuplicateLine(document: Document, terminal: Terminal)(implicit p: Platform) extends Action {
  def name: String        = "Duplicate Line"
  def mnemonic: Char      = 'D'
  val keys: ISeq[String]  = s"${p.menuModifier} pressed D" :: Nil

  def apply(): Unit = {
    val selection = terminal.selection
    val interval  = selection.getOrElse {
      val line = document.toLocation(terminal.offset).line
      Interval(document.startOffsetOf(line), document.endOffsetOf(line))
    }
    val snippet   = document.text(interval)
    val addition  = if (selection.isDefined) snippet else "\n%s".format(snippet)

    document.insert(interval.stop, addition)

    terminal.offset += addition.length

    for (selection <- selection) {
      terminal.selection = Some(selection.transformWith(_ + addition.length))
    }
  }
}