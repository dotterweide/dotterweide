/*
 *  UndoRedoAction.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor.controller

import dotterweide.editor.{Action, History}

import scala.collection.immutable.{Seq => ISeq}

private abstract class UndoRedoAction(history: History) extends Action {
  /** Recalculates the current enabled state. */
  protected def calcEnabled(): Boolean

  private var _enabled = calcEnabled()

  final override def enabled: Boolean = _enabled

  private def checkEnabled(): Unit = {
    val now = calcEnabled()
    if (now != _enabled) {
      _enabled = now
      notifyObservers(Action.EnabledChanged(enabled = now))
    }
  }

  history.onChange {
    checkEnabled()
  }
}

private class Redo(history: History) extends UndoRedoAction(history) {

  def name: String        = "Redo"
  def mnemonic: Char      = 'R'
  def keys: ISeq[String]  = "shift ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canRedo

  def apply(): Unit =
    history.redo()
}

private class Undo(history: History) extends UndoRedoAction(history) {

  def name: String        = "Undo"
  def mnemonic: Char      = 'U'
  def keys: ISeq[String]  = "ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canUndo

  def apply(): Unit =
    history.undo()
}