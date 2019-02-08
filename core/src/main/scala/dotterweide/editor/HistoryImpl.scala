/*
 *  HistoryImpl.scala
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

import dotterweide.document.Document

class HistoryImpl extends History {
  private var toUndo: List[Capture] = Nil
  private var toRedo: List[Capture] = Nil

  private var busy = false

  def capture(document: Document, terminal: Terminal)(block: => Unit): Unit = {
    if (busy)
      throw new IllegalStateException("Nested capture")

    busy = true
    val fire = try {
      var edits     = List.empty[UndoableEdit]
      val recorder  = edits ::= (_: UndoableEdit)

      document.onChange(recorder)
      terminal.onChange(recorder)

      block

      document.disconnect(recorder)
      terminal.disconnect(recorder)

      edits.exists(_.significant) && {
        val couldUndo = toUndo.nonEmpty
        val couldRedo = toRedo.nonEmpty
        toUndo ::= Capture(edits)
        toRedo = Nil
        !couldUndo || couldRedo
      }

    } finally {
      busy = false
    }

    if (fire) notifyObservers()
  }

  def canUndo: Boolean = toUndo.nonEmpty

  def undo(): Unit =
    toUndo match {
      case action :: tail =>
        action.undo()
        val fire = tail.isEmpty || toRedo.isEmpty
        toUndo = tail
        toRedo ::= action
        if (fire) notifyObservers()

      case _ =>
        throw new IllegalStateException("Nothing to undo")
    }

  def canRedo: Boolean = toRedo.nonEmpty

  def redo(): Unit =
    toRedo match {
      case action :: tail =>
        action.redo()
        val fire = tail.isEmpty || toUndo.isEmpty
        toRedo = tail
        toUndo ::= action
        if (fire) notifyObservers()

      case _ =>
        throw new IllegalStateException("Nothing to redo")
    }

  def clear(): Unit = {
    val fire = toUndo.nonEmpty || toRedo.nonEmpty
    toUndo = Nil
    toRedo = Nil
    if (fire) notifyObservers()
  }

  private case class Capture(edits: List[UndoableEdit]) {
    def undo(): Unit =
      edits.foreach(_.undo())

    def redo(): Unit =
      edits.reverse.foreach(_.redo())
  }
}