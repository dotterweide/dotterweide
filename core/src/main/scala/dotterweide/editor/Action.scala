/*
 *  Action.scala
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

package dotterweide.editor

import dotterweide.ObservableEvents

object Action {
  sealed trait Update
  final case class EnabledChanged (enabled: Boolean ) extends Update
  final case class NameChanged    (name   : String  ) extends Update
}
trait Action extends Function0[Unit] with ObservableEvents[Action.Update] {
  /** A list of keyboard shortcuts. */
  def keys: List[String]

  /** Name or title of the action. */
  def name: String

  /** Mnemonic character for menu display, or `'\0'` */
  def mnemonic: Char

  /** Whether the action is currently available or not. */
  def enabled: Boolean = true
}