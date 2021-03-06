/*
 *  PlatformImpl.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.impl

import java.awt.{GraphicsEnvironment, Toolkit}
import java.awt.event.InputEvent

import dotterweide.Platform

class PlatformImpl extends Platform {
  val menuModifier: String = {
    if (GraphicsEnvironment.isHeadless) "ctrl" else {
      val mask  = Toolkit.getDefaultToolkit.getMenuShortcutKeyMask
      val buf   = new StringBuilder
      if ((mask & InputEvent.SHIFT_MASK     ) != 0) buf.append("shift "   )
      if ((mask & InputEvent.CTRL_MASK      ) != 0) buf.append("ctrl "    )
      if ((mask & InputEvent.META_MASK      ) != 0) buf.append("meta "    )
      if ((mask & InputEvent.ALT_MASK       ) != 0) buf.append("alt "     )
      if ((mask & InputEvent.ALT_GRAPH_MASK ) != 0) buf.append("altGraph ")
      buf.result().trim
    }
  }

  private[this] val osName: String = sys.props("os.name")

  val isLinux  : Boolean = osName.contains("Linux")
  val isMac    : Boolean = osName.contains("Mac")
  val isWindows: Boolean = osName.contains("Windows")

  // println(s"menuModifier = $menuModifier")
}
