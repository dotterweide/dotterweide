/*
 *  Flash.scala
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

import dotterweide.{Interval, ObservableEvents}

/** An object that emits flashes, timed events associated with a text selection. */
trait Flash extends ObservableEvents[Flash.Update] {
  /** Emits a flash for a given duration in milliseconds. */
  def emit(duration: Int, interval: Interval, level: Int): Int

  /** Re-emits an event for the last flash (if it has not timed out yet), setting its `isError` to true. */
  def changeLevel(id: Int, newLevel: Int): Unit
}

object Flash {
  final val LevelInfo   = 0
  final val LevelWarn   = 1
  final val LevelError  = 2

  sealed trait Update
  case class Emit(id: Int, duration: Int, interval: Interval, level: Int) extends Update
  case class LevelChanged(id: Int, level: Int) extends Update
}

