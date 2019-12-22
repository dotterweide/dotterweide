/*
 *  Platform.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide

import dotterweide.impl.PlatformImpl

object Platform {
  def apply(): Platform = new PlatformImpl
}
/** A platform represents operation system specific details. */
trait Platform {
  /** Primary modifier key for "menu" commands.
    * This is `"ctrl"` on Linux and Windows, and `"meta"` on Mac.
    */
  def menuModifier: String
}
