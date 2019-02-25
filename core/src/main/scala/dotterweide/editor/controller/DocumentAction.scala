/*
 *  DocumentAction.scala
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

/** An action that updates its `enabled` state based on changes to the document or terminal. */
private abstract class DocumentAction(document: Document, terminal: Terminal) extends Action {
  /** Recalculates the current enabled state. */
  protected def calcEnabled(): Boolean

  private[this] var _enabled = calcEnabled()

  final override def enabled: Boolean = _enabled

  private def checkEnabled(): Unit = {
    val now = calcEnabled()
    if (now != _enabled) {
      _enabled = now
      notifyObservers(Action.EnabledChanged(enabled = now))
    }
  }

  document.onChange { _ =>
    checkEnabled()
  }
  terminal.onChange { _ =>
    checkEnabled()
  }
}