/*
 *  DocumentImpl.scala
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

package dotterweide.document.impl

import dotterweide.document.{Anchor, Bias, Document, Insertion, LinedString, Removal, Replacement}

import scala.collection.immutable.{Seq => ISeq}

class DocumentImpl(text0: String = "") extends Document {
  private[this] var ls      = new LinedString(text0)
  private[this] var anchors = List.empty[AnchorImpl]

  def length: Int     = ls.length
  def text  : String  = ls.toString

  def text_=(s: String): Unit =
    replace(0, length, s)

  def characters: CharSequence = ls

  def insert(offset: Int, chars: String): Unit = {
    check(offset)
    ls = ls.replace(start = offset, end = offset, chars = chars)
    updateAnchors(offset, offset, offset + chars.length)
    val evt = Insertion(this, offset = offset, chars = chars)
    notifyObservers(evt)
  }

  def remove(begin: Int, end: Int): Unit = {
    check(begin, end)
    val previous = ls.subSequence(begin, end)
    ls = ls.replace(begin, end, "")
    updateAnchors(begin, end, begin)
    val evt = Removal(this, start = begin, stop = end, before = previous)
    notifyObservers(evt)
  }

  def replace(begin: Int, end: Int, chars: String): Unit = {
    check(begin, end)
    val previous = ls.subSequence(begin, end)
    ls = ls.replace(begin, end, chars)
    updateAnchors(begin, end, begin + chars.length)
    val evt = Replacement(this, start = begin, stop = end, before = previous, now = chars)
    notifyObservers(evt)
  }

  private def updateAnchors(begin: Int, end: Int, end2: Int): Unit =
    anchors.foreach(_.update(begin, end, end2))

  private def check(offset: Int, parameter: String = "Offset"): Unit =
    if (offset < 0 || offset > length)
      throw new IndexOutOfBoundsException("%s (%d) must be within [%d; %d]".format(parameter, offset, 0, length))

  private def check(begin: Int, end: Int): Unit = {
    check(begin , "Begin" )
    check(end   , "End"   )

    if (begin > end)
      throw new IllegalArgumentException("Begin (%d) must be not greater than end (%d)".format(begin, end))
  }

  def createAnchorAt(offset: Int, bias: Bias): Anchor = {
    val anchor = new AnchorImpl(offset, bias)
    anchors ::= anchor
    anchor
  }

  protected def wraps: ISeq[Int] = ls.wraps

  private class AnchorImpl(var offset: Int, bias: Bias) extends Anchor {
    def dispose(): Unit =
      anchors = anchors.diff(this :: Nil)

    /** Adjusts the offset based on the document edit.
      *
      * @param begin  begin of edited span
      * @param end    former end of edited span (exclusive)
      * @param end2   new    end of edited span (exclusive)
      */
    def update(begin: Int, end: Int, end2: Int): Unit =
      if (begin < offset && end <= offset /* offset was at or after the old end */) {
        offset += end2 - end  // just add the difference
      } else if ((begin < offset && offset < end && end2 < offset /* offset was inside the erased bit */) ||
        (begin == end && begin == offset && bias == Bias.Right) /* right-biased anchor was at empty span */ ) {
        offset = end2
      }
  }
}
