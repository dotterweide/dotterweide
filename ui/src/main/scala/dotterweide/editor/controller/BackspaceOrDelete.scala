/*
 *  BackspaceOrDelete.scala
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

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.editor.{NamedEdit, Terminal, UndoableEdit}

trait BackspaceOrDelete extends NamedEdit {
  _: Product =>

  protected def document        : Document
  protected def terminal        : Terminal
  protected def offset          : Int
  protected def chars           : String
  protected def offsetBefore    : Int
  protected def selectionBefore : Option[Interval]

  def significant = true

  def name: String = productPrefix

  def undo(): Unit = {
    document.insert(offset, chars)
    terminal.offset     = offsetBefore
    terminal.selection  = selectionBefore
  }

  def redo(): Unit = {
    terminal.selection  = None
    terminal.offset     = offset
    document.remove(offset, offset + chars.length)
  }
}

object Backspace {
  /** Performs a backspace key press and returns the corresponding edit. */
  def apply(document: Document, terminal: Terminal, interval: Interval): Backspace = {
    require (interval.nonEmpty)
    val chars = document.text(interval)
    val res   = new Backspace(document, terminal, offset = interval.start, chars = chars,
      offsetBefore = terminal.offset, selectionBefore = terminal.selection)
    res.redo()
    res
  }
}
case class Backspace private (document: Document, terminal: Terminal, offset: Int, chars: String,
                              offsetBefore: Int, selectionBefore: Option[Interval])
  extends BackspaceOrDelete {

  def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
    case that: Backspace if this.document == that.document && this.terminal == that.terminal &&
      this.offset == that.offset + that.chars.length =>

      val m = copy(offset = that.offset, chars = that.chars + this.chars)
      Some(m)

    case _ => None
  }
}

object Delete {
  /** Performs a delete key press and returns the corresponding edit. */
  def apply(document: Document, terminal: Terminal, interval: Interval): Delete = {
    require (interval.nonEmpty)
    val chars = document.text(interval)
    val res   = new Delete(document, terminal, offset = interval.start, chars = chars,
      offsetBefore = terminal.offset, selectionBefore = terminal.selection)
    res.redo()
    res
  }
}
case class Delete private (document: Document, terminal: Terminal, offset: Int, chars: String,
                           offsetBefore: Int, selectionBefore: Option[Interval])
  extends BackspaceOrDelete {

  def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
    case that: Delete if this.document == that.document && this.terminal == that.terminal &&
      this.offset == that.offset =>

      val m = copy(chars = this.chars + that.chars)
      Some(m)

    case _ => None
  }
}
