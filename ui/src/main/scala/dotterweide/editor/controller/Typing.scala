/*
 *  Typing.scala
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

object Insert {
  /** Performs a text insertion, moving the cursor one character to the right,
    * returns the corresponding edit
    */
  def apply(document: Document, terminal: Terminal, chars: String, advance: Int): Insert = {
    val sel       = terminal.selection.map { it => (it.start, document.text(it)) }
    val offBefore = terminal.offset
    val offAfter  = sel.fold(offBefore)(_._1) + advance
    val res = new Insert(document, terminal, chars, offsetBefore = offBefore, offsetAfter = offAfter,
      selectionBefore = sel)
    res.redo()
    res
  }
}
case class Insert private(document: Document, terminal: Terminal, chars: String, offsetAfter: Int,
                          offsetBefore: Int, selectionBefore: Option[(Int, String)])
  extends Typing {

  protected def charsBefore: String = ""

  protected def update(newChars: String, newCharsBefore: String, newOffsetAfter: Int): Insert =
    copy(chars = newChars, offsetAfter = newOffsetAfter)
}

object Overwrite {
  /** Performs a text overwriting, moving the cursor one character to the right,
    * returns the corresponding edit
    */
  def apply(document: Document, terminal: Terminal, chars: String, advance: Int): Overwrite = {
    val sel         = terminal.selection.map { it => (it.start, document.text(it)) }
    val offBefore   = terminal.offset
    val offAfter    = sel.fold(offBefore)(_._1) + advance
    val line        = document.lineNumberOf(offBefore)
    val ovrStop     = document.endOffsetOf(line)
    val charsBefore = document.text(offBefore, math.min(ovrStop, offBefore + chars.length))
    val res = new Overwrite(document, terminal, chars, offsetBefore = offBefore,
      charsBefore = charsBefore, offsetAfter = offAfter, selectionBefore = sel)
    res.redo()
    res
  }
}
case class Overwrite private(document: Document, terminal: Terminal, chars: String, offsetAfter: Int,
                             charsBefore: String, offsetBefore: Int, selectionBefore: Option[(Int, String)])
  extends Typing {

  protected def update(newChars: String, newCharsBefore: String, newOffsetAfter: Int): Overwrite =
    copy(chars = newChars, offsetAfter = newOffsetAfter, charsBefore = newCharsBefore)
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
  extends Typing {

  protected def charsBefore: String = ""

  protected def update(newChars: String, newCharsBefore: String, newOffsetAfter: Int): NewLine =
    copy(chars = newChars, offsetAfter = newOffsetAfter)
}

abstract class Typing extends Product with NamedEdit {
  // ---- abstract ----

  protected def document        : Document
  protected def terminal        : Terminal
  protected def chars           : String
  protected def charsBefore     : String
  protected def offsetAfter     : Int
  protected def offsetBefore    : Int
  protected def selectionBefore : Option[(Int, String)]

  protected def update(newChars: String, newCharsBefore: String, newOffsetAfter: Int): Typing

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
        val b = charsBefore
        if (b.isEmpty) {
          document.remove(offsetBefore, offsetBefore + chars.length)
        } else {
          document.replace(offsetBefore, offsetBefore + chars.length, charsBefore)
        }
    }
  }

  def redo(): Unit = {
    selectionBefore match {
      case Some((selStart, remChars)) =>
        terminal.selection = None
        terminal.offset = selStart
        document.replace(Interval(selStart, selStart + remChars.length), chars)

      case None =>
        val b = charsBefore
        if (b.isEmpty) {
          document.insert(offsetBefore, chars)
        } else {
          // println(s"document.replace($offsetBefore, ${offsetBefore + charsBefore.length}, $chars)")
          document.replace(offsetBefore, offsetBefore + charsBefore.length, chars)
        }
    }
    terminal.offset = offsetAfter
  }

  protected def charStart: Int =
    selectionBefore.fold(offsetBefore)(_._1)

  protected def charStop: Int =
    charStart + chars.length

  override def tryMerge(succ: UndoableEdit): Option[NamedEdit] = succ match {
    case that: Typing if this.productPrefix == that.productPrefix &&
      this.document == that.document && this.terminal == that.terminal &&
      that.charStart == this.charStop /* so we can concat newChars */ &&
      that.selectionBefore.isEmpty =>

      val newChars        = this.chars        + that.chars
      val newCharsBefore  = this.charsBefore  + that.charsBefore  // XXX TODO do we need to verify that concat is ok?
      val m = update(newChars = newChars, newCharsBefore = newCharsBefore, newOffsetAfter = that.offsetAfter)
      Some(m)

    case _ => None
  }
}
