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
import dotterweide.editor.impl.FontSettingsImpl

object FontSettings {
  val Default: FontSettings = apply(
    family        = "Monospaced",
    size          = 14f,
    stretch       = 1.0f,
    lineSpacing   = 1.12f
  )

  def apply(family: String = "Monospaced", size: Float = 14f, stretch: Float = 1.0f,
            lineSpacing: Float = 1.12f): FontSettings =
    new FontSettingsImpl(
      family0       = family,
      size0         = size,
      stretch0      = stretch,
      lineSpacing0  = lineSpacing
    )
}
trait FontSettings extends Observable {
  var family      : String

  /** The nominal font size in points. */
  var size        : Float

  /** A vertical stretch factor that can be used to obtain more condensed fonts.
    * A value of 1.0 would indicate no stretching.
    */
  var stretch     : Float

  /** The line spacing, a factor relative to the font height.
    * For example, a value of 1.0 would indicate 100% line spacing,
    * and a value of 1.1 would indicate 110% line spacing or 10% larger
    * spacing than the font height.
    */
  var lineSpacing : Float
}