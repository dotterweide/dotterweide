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

  protected def calcName(): String

  private var _enabled  = calcEnabled ()
  private var _name     = calcName    ()

  final override def enabled: Boolean = _enabled
  final override def name   : String  = _name

  private def checkEnabled(): Unit = {
    val enabledNow = calcEnabled()
    if (_enabled != enabledNow) {
      _enabled = enabledNow
      notifyObservers(Action.EnabledChanged(enabled = enabledNow))
    }
  }

  private def checkName(): Unit = {
    val nameNow = calcName()
    if (_name != nameNow) {
      _name = nameNow
      notifyObservers(Action.NameChanged(name = nameNow))
    }
  }

  history.onChange {
    checkEnabled()
    checkName()
  }
}

private class Redo(history: History) extends UndoRedoAction(history) {

  def mnemonic: Char      = 'R'
  def keys: ISeq[String]  = "shift ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canRedo

  protected def calcName(): String = if (enabled) s"Redo ${history.redoName}" else "Redo"

  def apply(): Unit =
    history.redo()
}

private class Undo(history: History) extends UndoRedoAction(history) {

  def mnemonic: Char      = 'U'
  def keys: ISeq[String]  = "ctrl pressed Z" :: Nil

  protected def calcEnabled(): Boolean = history.canUndo

  protected def calcName(): String = if (enabled) s"Undo ${history.undoName}" else "Undo"

  def apply(): Unit =
    history.undo()
}