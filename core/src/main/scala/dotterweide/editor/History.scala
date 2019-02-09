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
  * Notifies observers when `canUndo` or `canRedo` change.
  */
trait History extends Observable {
  def capture(name: String, document: Document, terminal: Terminal)(block: => Unit): Unit

  def add(edit: NamedEdit): Unit

  def canUndo: Boolean

  /** Throws an exception if `!canUndo` */
  def undo(): Unit

  /** Throws an exception if `!canUndo` */
  def undoName: String

  def canRedo: Boolean

  /** Throws an exception if `!canRedo` */
  def redo(): Unit

  /** Throws an exception if `!canRedo` */
  def redoName: String

  def clear(): Unit
}