/*
 *  FontSettings.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor

import dotterweide.Observable

object FontSettings {
  val Default: FontSettings = new FontSettingsImpl(
    family0       = "Monospaced",
    size0         = 14,
    lineSpacing0  = 1.12f
  )
}
trait FontSettings extends Observable {
  def family: String

  def size: Int

  def lineSpacing: Float
}