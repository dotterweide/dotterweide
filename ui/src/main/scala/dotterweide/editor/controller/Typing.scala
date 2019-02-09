/*
 *  TypingOrNewLine.scala
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

object Typing {
  /** Performs a text insertion, moving the cursor one character to the right,
    * returns the corresponding edit
    */
  def apply(document: Document, terminal: Terminal, chars: String, advance: Int): Typing = {
    val sel       = terminal.selection.map { it => (it.start, document.text(it)) }
    val offBefore = terminal.offset
    val offAfter  = sel.fold(offBefore)(_._1) + advance
    val res = new Typing(document, terminal, chars, offsetBefore = offBefore, offsetAfter = offAfter,
      selectionBefore = sel)
    res.redo()
    res
  }
}
case class Typing private (document: Document, terminal: Terminal, chars: String, offsetAfter: Int,
                           offsetBefore: Int, selectionBefore: Option[(Int, String)])
  extends TypingOrNewline {
  
  protected def update(newChars: String, newOffsetAfter: Int): Typing = 
    copy(chars = newChars, offsetAfter = newOffsetAfter)
}

object NewLine {
  /** Performs a new line insertion, moving the cursor one character to particular position,
    * returns the corresponding edit
    */
  def apply(document: Document, terminal: Terminal, chars: String, offsetAfter: Int): NewLine = {
    val sel       = terminal.selection.map { it => (it.start, document.text(it)) }
    val offBefore = terminal.offset
    val res = new NewLine(document, terminal, chars, offsetBefore = offBefore, offsetAfter = offsetAfter,
      selectionBefore = sel)
    res.redo()
    res
  }
}
case class NewLine private (document: Document, terminal: Terminal, chars: String, offsetAfter: Int,
                           offsetBefore: Int, selectionBefore: Option[(Int, String)])
  extends TypingOrNewline {

  protected def update(newChars: String, newOffsetAfter: Int): NewLine =
    copy(chars = newChars, offsetAfter = newOffsetAfter)
}

abstract class TypingOrNewline extends Product with NamedEdit {
  // ---- abstract ----

  protected def document        : Document
  protected def terminal        : Terminal
  protected def chars           : String
  protected def offsetAfter     : Int
  protected def offsetBefore    : Int
  protected def selectionBefore : Option[(Int, String)]

  protected def update(newChars: String, newOffsetAfter: Int): TypingOrNewline

  // ---- impl ----

  def name = "Typing"

  def significant: Boolean = true

  def undo(): Unit = {
    selectionBefore match {
      case Some((selStart, remChars)) =>
        terminal.offset     = selStart
        document.replace(Interval(selStart, selStart + chars.length), remChars)
        terminal.offset     = offsetBefore
        terminal.selection  = Some(Interval(selStart, selStart + remChars.length))

      case None =>
        terminal.offset = offsetBefore
        document.remove(offsetBefore, offsetBefore + chars.length)
    }
  }

  def redo(): Unit = {
    selectionBefore match {
      case Some((selStart, remChars)) =>
        terminal.selection = None
        terminal.offset = selStart
        document.replace(Interval(selStart, selStart + remChars.length), chars)

      case None =>
        document.insert(offsetBefore, chars)
    }
    terminal.offset = offsetAfter
  }

  override def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
    case that: TypingOrNewline if this.productPrefix == that.productPrefix &&
      this.document == that.document && this.terminal == that.terminal &&
      that.offsetBefore == this.offsetAfter && that.selectionBefore.isEmpty =>

      val m = update(newChars = this.chars + that.chars, newOffsetAfter = that.offsetAfter)
      Some(m)

    case _ => None
  }
}
