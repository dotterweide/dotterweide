/*
 *  ExampleAction.scala
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

package dotterweide.ide.action

import dotterweide.ide.EditorTab

import scala.swing.Action

/** An action to replace the editor contents with a code example. */
class ExampleAction(title0: String, mnemonic0: Char, tab: EditorTab, code: String) extends Action(title0) {
  mnemonic = mnemonic0

  def apply(): Unit =
    tab.text = code.filterNot(_ == '\r').trim
}