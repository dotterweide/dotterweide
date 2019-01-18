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

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{Action, Terminal, History}

private class HistoricalAction(delegate: Action, document: Document, terminal: Terminal, history: History)
  extends Action {

  def keys: Seq[String] = delegate.keys

  override def enabled: Boolean = delegate.enabled

  delegate.onChange(notifyObservers())

  def apply(): Unit =
    history.recording(document, terminal) {
      delegate()
    }
}