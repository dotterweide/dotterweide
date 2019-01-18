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

package com.pavelfatin.toyide.ide

import javax.swing.{KeyStroke, SwingUtilities}

import com.pavelfatin.toyide.editor.{Action => _Action}

import scala.swing.Action

private class ActionAdapter(title: String, key: Char, anAction: _Action) extends Action(title) {
  mnemonic    = key
  accelerator = Some(KeyStroke.getKeyStroke(anAction.keys.head))
  enabled     = anAction.enabled

  anAction.onChange {
    SwingUtilities.invokeLater(new Runnable {
      def run(): Unit = {
        enabled = anAction.enabled
      }
    })
  }

  def apply(): Unit = anAction()
}