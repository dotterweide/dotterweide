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

  private var _canUndo  = false
  private var _canRedo  = false
  private var _undoName = ""
  private var _redoName = ""

  private val Empty   = new Compound("", Nil, significant = false)

  // add a non-significant edit puts it into pending limbo,
  // because we do not yet want to purge the redo tree at this stage
  private var pending = Empty

  def add(edit: NamedEdit): Unit = {
    if (edit.significant) {
      val undoNameOld = _undoName
      val canUndoOld  = _canUndo
      val canRedoOld  = _canRedo

      if (pending.nonEmpty) {
        toUndo  ::= pending
        pending   = Empty
      }

      toUndo = toUndo match {
        case head :: tail if !_blockMerge =>
          head.tryMerge(edit) match {
            case Some(merged) => merged :: tail
            case None         => edit   :: toUndo
          }

        case _ => edit :: toUndo
      }
      _canUndo  = true
      _undoName = toUndo.head.name
      toRedo    = Nil
      _canRedo  = false
      _redoName = ""

      val fire = !canUndoOld || canRedoOld || (canUndoOld && undoNameOld != _undoName)
      if (fire) notifyObservers()

    } else {
      if (_canUndo) pending = pending.merge(edit)
    }

    _blockMerge = false
  }

  def capture[A](name: String, document: Document, terminal: Terminal)(block: => A): A = {
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

      val res = block

      document.disconnect(recorder)
      terminal.disconnect(recorder)

      if (edits.nonEmpty) {
        val hasSig = edits.exists(_.significant)
        val edit = new Compound(name, edits, significant = hasSig)
        add(edit)
      }

      res

    } finally {
      busy = false
    }
  }

  def canUndo: Boolean = _canUndo // toUndo.nonEmpty

  def undoName: String = _undoName // toUndo.head.name

  def undo(): Unit =
    if (pending.nonEmpty) {
      pending.undo()
      pending = Empty
    } else {
      toUndo match {
        case action :: tail =>
          action.undo()
          val canRedoOld  = _canRedo
          val undoNameOld = _undoName
          val redoNameOld = _redoName
          toUndo = tail
          toRedo ::= action
          _canUndo  = tail.nonEmpty
          _canRedo  = true
          if (action.significant) {
            _redoName     = action.name
            _undoName     = toUndo match {
              case head :: _ if head.significant  => head.name
              case _ :: pen :: _                  => pen.name
              case _                              => ""
            }
          }

          val fire = !_canUndo || !canRedoOld || _undoName != undoNameOld || _redoName != redoNameOld
          blockMerge()
          if (fire) notifyObservers()

        case _ =>
          throw new IllegalStateException("Nothing to undo")
      }
    }

  def canRedo: Boolean = _canRedo // toRedo.nonEmpty

  def redoName: String = _redoName // toRedo.head.name

  def redo(): Unit = {
    if (pending.nonEmpty) {
      pending.undo()
      pending = Empty
    }
    toRedo match {
      case action :: tail =>
        action.redo()
        val canUndoOld  = _canUndo
        val undoNameOld = _undoName
        val redoNameOld = _redoName
        toRedo = tail
        toUndo ::= action
        _canRedo  = tail.nonEmpty
        _canUndo  = true
        if (action.significant) {
          _undoName     = action.name
          _redoName     = toRedo match {
            case head :: _ if head.significant  => head.name
            case _ :: pen :: _                  => pen.name
            case _                              => ""
          }
        }

        val fire = !_canRedo || !canUndoOld || _undoName != undoNameOld || _redoName != redoNameOld
        blockMerge()
        if (fire) notifyObservers()

      case _ =>
        throw new IllegalStateException("Nothing to redo")
    }
  }

  def clear(): Unit = {
    val fire = toUndo.nonEmpty || toRedo.nonEmpty
    _canUndo  = false
    toUndo    = Nil
    _undoName = ""
    _canRedo  = false
    toRedo    = Nil
    _redoName = ""
    pending   = Empty
    if (fire) notifyObservers()
  }

  private class Compound(val name: String, val edits: List[UndoableEdit], val significant: Boolean)
    extends NamedEdit {

    def isEmpty : Boolean = edits.isEmpty
    def nonEmpty: Boolean = edits.nonEmpty

    private def mergeEdits(succ: List[UndoableEdit]): List[UndoableEdit] =
      (succ, edits) match {
        case (init :+ succLast, head :: tail) =>
          head.tryMerge(succLast) match {
            case Some(merge)  => init ::: merge :: tail
            case None         => succ ::: edits
          }

        case _ => succ ::: edits
      }

    def merge(succ: UndoableEdit): Compound = {
      val succEdits = succ match {
        case a: Compound  => a.edits
        case _            => succ :: Nil
      }
      val newEdits = mergeEdits(succEdits)
      new Compound(name, newEdits, significant = significant || succ.significant)
    }

    def undo(): Unit =
      edits.foreach(_.undo())

    def redo(): Unit =
      edits.reverse.foreach(_.redo())

    def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
      case that: Compound if !this.significant && !that.significant =>
        val newEdits = mergeEdits(that.edits)
        val m = new Compound(name, edits = newEdits, significant = significant)
        Some(m)

      case _ => None
    }
  }
}