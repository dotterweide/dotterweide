/*
 *  History.scala
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

import dotterweide.Observable
import dotterweide.document.Document

/** Undo-redo management.
  *
  * Notifies observers when `canUndo`, `canRedo`, `undoName`, or `redoName` change.
  */
trait History extends Observable {
  /** Creates an automatic edit from tracing events on a document and terminal.
    * The edit, if not empty, will be added to the history before returning.
    *
    * @param name       the name of the resulting edit
    * @param document   the document to temporarily trace
    * @param terminal   the terminal to temporarily trace
    * @param block      an action that produces events as a side effect. These events
    *                   will make up the edit.
    */
  def capture(name: String, document: Document, terminal: Terminal)(block: => Unit): Unit

  /** Add another edit to the history.
    * Unless merging is blocked, it tries to merge this edit
    * with the most recent edit. Afterwards,
    * the internal merge-block flag is cleared.
    */
  def add(edit: NamedEdit): Unit

  /** Disallow the merging of the next edit to be added.
    * This can be used to avoid merging edits if the editor
    * component was temporarily unfocused, for example.
    */
  def blockMerge(): Unit

  /** Whether there are undoable edits and thus `undo` and ` undoName` may be called. */
  def canUndo: Boolean

  /** Throws an exception if `!canUndo` */
  def undo(): Unit

  /** Throws an exception if `!canUndo` */
  def undoName: String

  /** Whether there are edits that can be redone, and thus whether `redo` and ` redoName` may be called. */
  def canRedo: Boolean

  /** Throws an exception if `!canRedo` */
  def redo(): Unit

  /** Throws an exception if `!canRedo` */
  def redoName: String

  /** Clears the history, removing all edits. Afterwards, `canUndo` and `canRedo` will return `false`. */
  def clear(): Unit
}