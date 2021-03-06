/*
 *  HistoricalAction.scala
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
import dotterweide.editor.{Action, History, Terminal}

import scala.collection.immutable.{Seq => ISeq}

private class HistoricalAction(peer: Action, document: Document, terminal: Terminal, history: History)
  extends Action {

  val keys: ISeq[String]  = peer.keys

  override def name     : String  = peer.name
  override def mnemonic : Char    = peer.mnemonic
  override def enabled  : Boolean = peer.enabled

  peer.onChange(evt => notifyObservers(evt))

  def apply(): Unit =
    history.capture(name, document, terminal) {
      peer()
    }
}