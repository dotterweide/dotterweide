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

package com.pavelfatin.toyide.document

sealed trait DocumentEvent {
  def undo(document: Document): Unit
  def redo(document: Document): Unit

  def asReplacement: Replacement
}

case class Insertion(offset: Int, chars: CharSequence) extends DocumentEvent {
  def undo(document: Document): Unit =
    document.remove(offset, offset + chars.length)

  def redo(document: Document): Unit =
    document.insert(offset, chars.toString)

  def asReplacement = Replacement(offset, offset, "", chars)
}

case class Removal(begin: Int, end: Int, before: CharSequence) extends DocumentEvent {
  def undo(document: Document): Unit =
    document.insert(begin, before.toString)

  def redo(document: Document): Unit =
    document.remove(begin, end)

  def asReplacement = Replacement(begin, end, before, "")
}

case class Replacement(begin: Int, end: Int, before: CharSequence, after: CharSequence) extends DocumentEvent {
  def undo(document: Document): Unit =
    document.replace(begin, begin + after.length, before.toString)

  def redo(document: Document): Unit =
    document.replace(begin, end, after.toString)

  def asReplacement: Replacement = this
}
