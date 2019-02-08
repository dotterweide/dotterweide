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

import dotterweide.editor.UndoableEdit

sealed trait DocumentEvent extends UndoableEdit{
  def document: Document

  def significant: Boolean = true

  def asReplacement: Replacement
}

case class Insertion(document: Document, offset: Int, chars: CharSequence) extends DocumentEvent {
  def undo(): Unit =
    document.remove(offset, offset + chars.length)

  def redo(): Unit =
    document.insert(offset, chars.toString)

  def asReplacement = Replacement(document, begin = offset, end = offset, before = "", now = chars)
}

case class Removal(document: Document, begin: Int, end: Int, before: CharSequence) extends DocumentEvent {
  def undo(): Unit =
    document.insert(offset = begin, before.toString)

  def redo(): Unit =
    document.remove(begin = begin, end = end)

  def asReplacement = Replacement(document, begin = begin, end = end, before = before, now = "")
}

case class Replacement(document: Document, begin: Int, end: Int, before: CharSequence, now: CharSequence)
  extends DocumentEvent {

  def undo(): Unit =
    document.replace(begin, begin + now.length, before.toString)

  def redo(): Unit =
    document.replace(begin, end, now.toString)

  def asReplacement: Replacement = this
}
