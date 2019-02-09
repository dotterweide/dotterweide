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

  private var toUndo: List[NamedEdit] = Nil
  private var toRedo: List[NamedEdit] = Nil

  private var busy = false

  def add(edit: NamedEdit): Unit = {
    val oldUndo   = toUndo
    val couldUndo = oldUndo.nonEmpty
    val couldRedo = toRedo.nonEmpty

    toUndo match {
      case head :: tail =>
        head.tryMerge(edit) match {
          case Some(merged) => toUndo = merged :: tail
          case None         => toUndo ::= edit
        }

      case _ => toUndo ::= edit
    }

    toRedo = Nil
    val fire = !couldUndo || couldRedo || (couldUndo && oldUndo.head.name != toUndo.head.name)
    if (fire) notifyObservers()
  }

  def capture(name: String, document: Document, terminal: Terminal)(block: => Unit): Unit = {
    if (busy)
      throw new IllegalStateException("Nested capture")

    busy = true
    try {
      var edits     = List.empty[UndoableEdit]
      val recorder  = edits ::= (_: UndoableEdit)

      document.onChange(recorder)
      terminal.onChange(recorder)

      block

      document.disconnect(recorder)
      terminal.disconnect(recorder)

      if (edits.exists(_.significant)) {
        val edit = new Action(name, edits)
        add(edit)
      }

    } finally {
      busy = false
    }
  }

  def canUndo: Boolean = toUndo.nonEmpty

  def undoName: String = toUndo.head.name

  def undo(): Unit =
    toUndo match {
      case action :: tail =>
        action.undo()
        val fire = tail.isEmpty || toRedo.isEmpty ||
          (action.name != tail.head.name) || (action.name != toRedo.head.name)
        toUndo = tail
        toRedo ::= action
        if (fire) notifyObservers()

      case _ =>
        throw new IllegalStateException("Nothing to undo")
    }

  def canRedo: Boolean = toRedo.nonEmpty

  def redoName: String = toRedo.head.name

  def redo(): Unit =
    toRedo match {
      case action :: tail =>
        action.redo()
        val fire = tail.isEmpty || toUndo.isEmpty ||
          (action.name != tail.head.name) || (action.name != toUndo.head.name)
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

//  private sealed abstract class Action(val name: String) {
//    def undo(): Unit
//    def redo(): Unit
//  }
//
//  private class SingleAction(name: String, edit: UndoableEdit) extends Action(name) {
//    def undo(): Unit = edit.undo()
//    def redo(): Unit = edit.redo()
//  }

  private class Action(val name: String, edits: List[UndoableEdit]) extends NamedEdit {
    def undo(): Unit =
      edits.foreach(_.undo())

    def redo(): Unit =
      edits.reverse.foreach(_.redo())

    def tryMerge(succ: UndoableEdit): Option[NamedEdit] = None

    def significant: Boolean = true
  }
}