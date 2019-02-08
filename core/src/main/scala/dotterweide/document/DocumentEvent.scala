/*
 *  DocumentEvent.scala
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

package dotterweide.document

import dotterweide.Interval
import dotterweide.editor.UndoableEdit

sealed trait DocumentEvent extends UndoableEdit {
  def document: Document

  def significant: Boolean = true

  def asReplacement: Replacement
}

case class Insertion(document: Document, offset: Int, chars: CharSequence) extends DocumentEvent {
  def undo(): Unit =
    document.remove(offset, offset + chars.length)

  def redo(): Unit =
    document.insert(offset, chars.toString)

  def interval: Interval = Interval(offset, offset + chars.length)

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = succ match {
    case that: Insertion if this.document == that.document && this.interval.touches(that.offset) =>
      val d = that.offset - this.offset
      val p = chars.subSequence(0, d).toString + that.chars.toString + chars.subSequence(d, chars.length()).toString
      val m = copy(chars = p)
      Some(m)

    case _ => None
  }

  def asReplacement = Replacement(document, start = offset, stop = offset, before = "", now = chars)
}

case class Removal(document: Document, start: Int, stop: Int, before: CharSequence) extends DocumentEvent {

  require (before.length == stop - start)

  def undo(): Unit =
    document.insert(offset = start, before.toString)

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = None // XXX TODO

  def redo(): Unit =
    document.remove(begin = start, end = stop)

  def asReplacement = Replacement(document, start = start, stop = stop, before = before, now = "")
}

case class Replacement(document: Document, start: Int, stop: Int,
                       before: CharSequence, now: CharSequence)
  extends DocumentEvent {

  require (before.length == stop - start)

  def tryMerge(succ: UndoableEdit): Option[UndoableEdit] = None // XXX TODO

  def undo(): Unit =
    document.replace(start, start + now.length, before.toString)

  def redo(): Unit =
    document.replace(start, stop, now.toString)

  def asReplacement: Replacement = this
}
