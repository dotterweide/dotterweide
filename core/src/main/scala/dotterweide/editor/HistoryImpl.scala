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

  private var busy        = false
  private var _blockMerge = false

  def blockMerge(): Unit =
    _blockMerge = true

  def add(edit: NamedEdit): Unit = {
//    if (edit.significant) {
      val oldUndo   = toUndo
      val couldUndo = oldUndo.nonEmpty
      val couldRedo = toRedo.nonEmpty

      toUndo = toUndo match {
        case head :: tail if !_blockMerge =>
          head.tryMerge(edit) match {
            case Some(merged) => merged :: tail
            case None         => edit   :: toUndo
          }

        case _ => edit :: toUndo
      }

      toRedo      = Nil
      val fire = !couldUndo || couldRedo || (couldUndo && oldUndo.head.name != toUndo.head.name)
      if (fire) notifyObservers()

//    } else {
//      val a: Action = pending match {
//        case Some(aOld) => aOld.append(edit)
//        case None       =>
//          edit match {
//            case a0: Action => a0
//            case _          => new Action(edit.name, edit :: Nil, significant = false)
//          }
//      }
//      pending = Some(a)
//    }

    _blockMerge = false
  }

  def capture(name: String, document: Document, terminal: Terminal)(block: => Unit): Unit = {
    if (busy)
      throw new IllegalStateException("Nested capture")

    busy = true
    try {
      var edits     = List.empty[UndoableEdit]
      val recorder  = { edit: UndoableEdit =>
        edits = edits match {
          case head :: tail =>
            head.tryMerge(edit) match {
              case Some(merged) => merged :: tail
              case None         => edit   :: edits
            }

          case _ => edit :: edits
        }
      }

      document.onChange(recorder)
      terminal.onChange(recorder)

      block

      document.disconnect(recorder)
      terminal.disconnect(recorder)

      if (edits.nonEmpty) {
        val hasSig = edits.exists(_.significant)
        val edit = new Action(name, edits, significant = hasSig)
        if (hasSig) add(edit)
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
        blockMerge()
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
        blockMerge()
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

  private class Action(val name: String, edits: List[UndoableEdit], val significant: Boolean)
    extends NamedEdit {

    def append(that: UndoableEdit): Action = {
      val more = that match {
        case a: Action  => a.edits
        case _          => that :: Nil
      }
      new Action(name, edits ::: more, significant = significant || that.significant)
    }

    def undo(): Unit =
      edits.foreach(_.undo())

    def redo(): Unit =
      edits.reverse.foreach(_.redo())

    def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
      case that: Action if !this.significant && !that.significant =>
        val m = new Action(name, edits = this.edits ::: that.edits, significant = significant)
        Some(m)

      case _ => None
    }
  }
}