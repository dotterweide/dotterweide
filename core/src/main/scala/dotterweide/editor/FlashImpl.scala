/*
 *  FlashImpl.scala
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

import java.util.concurrent.atomic.AtomicInteger

import dotterweide.Interval

class FlashImpl extends Flash {
  private[this] val _id = new AtomicInteger(0)

  def emit(duration: Int, interval: Interval, level: Int): Int = {
    val id = _id.getAndIncrement()
    notifyObservers(Flash.Emit(id = id, duration = duration, interval = interval, level = level))
    id
  }

  def changeLevel(id: Int, newLevel: Int): Unit =
    notifyObservers(Flash.LevelChanged(id = id, level = newLevel))
}
