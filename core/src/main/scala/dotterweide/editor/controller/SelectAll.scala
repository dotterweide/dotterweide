/*
 *  SelectAll.scala
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
import dotterweide.{Interval, Platform}

import scala.collection.immutable.{Seq => ISeq}

private class SelectAll(document: Document, terminal: Terminal)(implicit p: Platform) extends Action {
  def name: String        = "Select All"
  def mnemonic: Char      = 'A'
  val keys: ISeq[String]  = s"${p.menuModifier} pressed A" :: Nil

  def apply(): Unit =
    terminal.selection = Some(Interval(0, document.length))
}