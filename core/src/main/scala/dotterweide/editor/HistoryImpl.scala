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

import dotterweide.document.{Document, DocumentEvent}

class HistoryImpl extends History {
  private var toUndo: List[Action] = Nil
  private var toRedo: List[Action] = Nil

  private var busy = false

  def capture(document: Document, terminal: Terminal)(block: => Unit): Unit = {
    if (busy)
      throw new IllegalStateException("Nested capture")

    busy = true
    var events = List.empty[Any]

    val recorder = events ::= (_: Any)

    document.onChange(recorder)
    terminal.onChange(recorder)

    block

    document.disconnect(recorder)
    terminal.disconnect(recorder)

    if (events.exists(_.isInstanceOf[DocumentEvent])) {
      toUndo ::= Action(document, terminal, events)
      toRedo = Nil
    }

    busy = false
  }

  def canUndo: Boolean = toUndo.nonEmpty

  def undo(): Unit = {
    if (!canUndo)
      throw new IllegalStateException("Nothing to undo")

    toUndo.headOption.foreach { action =>
      action.undo()
      toUndo = toUndo.tail
      toRedo ::= action
    }
  }

  def canRedo: Boolean = toRedo.nonEmpty

  def redo(): Unit = {
    if (!canRedo)
      throw new IllegalStateException("Nothing to redo")

    toRedo.headOption.foreach { action =>
      action.redo()
      toRedo = toRedo.tail
      toUndo ::= action
    }
  }

  def clear(): Unit = {
    toUndo = Nil
    toRedo = Nil
  }

  private case class Action(document: Document, terminal: Terminal, events: List[Any]) {
    def undo(): Unit =
      events.foreach {
        case it: DocumentEvent => it.undo(document)
        case it: TerminalEvent => it.undo(terminal)
      }

    def redo(): Unit =
      events.reverse.foreach {
        case it: DocumentEvent => it.redo(document)
        case it: TerminalEvent => it.redo(terminal)
      }
  }
}