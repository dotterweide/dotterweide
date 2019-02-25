/*
 *  ActionAdapter.scala
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

package dotterweide.ide

import dotterweide.editor.{Action => _Action}
import javax.swing.KeyStroke

import scala.swing.{Action, Swing}

/** Wraps an `editor.Action` inside a `scala.swing.Action`. */
class ActionAdapter(peer: _Action) extends Action(peer.name) {
  mnemonic    = peer.mnemonic
  accelerator = Option(KeyStroke.getKeyStroke(peer.keys.headOption.orNull))
  enabled     = peer.enabled

  peer.onChange {
    case _Action.EnabledChanged(value) =>
      Swing.onEDT {
        enabled = value
      }
    case _Action.NameChanged(value) =>
      Swing.onEDT {
        title = value
      }
  }

  def apply(): Unit = peer()
}