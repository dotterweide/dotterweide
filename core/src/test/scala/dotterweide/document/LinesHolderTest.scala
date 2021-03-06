/*
 *  LinesHolderTest.scala
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

import org.junit.Test
import org.junit.Assert._

class LinesHolderTest {
  @Test
  def linesCount(): Unit = {
    assertEquals(1, Lines(0).linesCount)
    assertEquals(1, Lines(5).linesCount)
    assertEquals(2, Lines(5, List(1)).linesCount)
    assertEquals(3, Lines(5, List(1, 3)).linesCount)
    assertEquals(3, Lines(5, List(1, 2)).linesCount)
  }

  @Test
  def lineIndex(): Unit = {
    assertEquals(0, Lines(0).lineNumberOf(0))
    assertEquals(0, Lines(1).lineNumberOf(0))
    assertEquals(0, Lines(2).lineNumberOf(1))
    assertEquals(0, Lines(5, List(1)).lineNumberOf(0))
    assertEquals(0, Lines(5, List(1)).lineNumberOf(1))
    assertEquals(1, Lines(5, List(1)).lineNumberOf(2))
    assertEquals(2, Lines(7, List(1, 5)).lineNumberOf(6))
    assertEquals(2, Lines(7, List(1, 5)).lineNumberOf(7))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def lineIndexGreater(): Unit = {
    Lines(0).lineNumberOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def lineIndexLess(): Unit = {
    Lines(0).lineNumberOf(-1)
  }

  @Test
  def startOffset(): Unit = {
    assertEquals(0, Lines(0).startOffsetOf(0))
    assertEquals(0, Lines(1).startOffsetOf(0))
    assertEquals(0, Lines(1, List(0)).startOffsetOf(0))
    assertEquals(1, Lines(1, List(0)).startOffsetOf(1))
    assertEquals(0, Lines(5, List(1)).startOffsetOf(0))
    assertEquals(2, Lines(5, List(1)).startOffsetOf(1))
    assertEquals(6, Lines(7, List(1, 5)).startOffsetOf(2))
    assertEquals(7, Lines(7, List(6)).startOffsetOf(1))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def startOffsetGreater(): Unit = {
    Lines(5).startOffsetOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def startOffsetLess(): Unit = {
    Lines(5).startOffsetOf(-1)
  }

  @Test
  def endOffset(): Unit = {
    assertEquals(0, Lines(0).endOffsetOf(0))
    assertEquals(1, Lines(1).endOffsetOf(0))
    assertEquals(1, Lines(5, List(1)).endOffsetOf(0))
    assertEquals(5, Lines(5, List(1)).endOffsetOf(1))
    assertEquals(5, Lines(7, List(1, 5)).endOffsetOf(1))
    assertEquals(7, Lines(7, List(1, 5)).endOffsetOf(2))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def endOffsetGreater(): Unit = {
    Lines(5).endOffsetOf(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def endOffsetLess(): Unit = {
    Lines(5).endOffsetOf(-1)
  }

  @Test
  def toLocation(): Unit = {
    assertEquals(Location(0, 0), Lines(0).toLocation(0))
    assertEquals(Location(0, 3), Lines(5).toLocation(3))
    assertEquals(Location(1, 2), Lines(7, List(3)).toLocation(6))
    assertEquals(Location(2, 3), Lines(9, List(3, 5)).toLocation(9))
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def loLocationGreater(): Unit = {
    Lines(0).toLocation(1)
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def loLocationLess(): Unit = {
    Lines(0).toLocation(-1)
  }

  @Test
  def toOffset(): Unit = {
    assertEquals(Some(0), Lines(0).toOffset(Location(0, 0)))
    assertEquals(None   , Lines(0).toOffset(Location(0, 1)))
    assertEquals(None   , Lines(0).toOffset(Location(1, 0)))
    assertEquals(Some(3), Lines(5).toOffset(Location(0, 3)))
    assertEquals(Some(6), Lines(7, List(3)).toOffset(Location(1, 2)))
    assertEquals(None   , Lines(7, List(3)).toOffset(Location(2, 0)))
    assertEquals(None   , Lines(7, List(3)).toOffset(Location(0, 4)))
  }

  @Test
  def toNearestOffset(): Unit = {
    assertEquals(0, Lines(0).toNearestOffset(Location(0, 0)))
    assertEquals(0, Lines(0).toNearestOffset(Location(0, 1)))
    assertEquals(0, Lines(0).toNearestOffset(Location(1, 0)))
    assertEquals(3, Lines(5).toNearestOffset(Location(0, 3)))
    assertEquals(6, Lines(7, List(3)).toNearestOffset(Location(1, 2)))
    assertEquals(4, Lines(7, List(3)).toNearestOffset(Location(2, 0)))
    assertEquals(3, Lines(7, List(3)).toNearestOffset(Location(0, 4)))

    assertEquals(3, Lines(9, List(3)).toNearestOffset(Location(0, 9)))
    assertEquals(9, Lines(9, List(3)).toNearestOffset(Location(1, 9)))

    assertEquals(5, Lines(9, List(3)).toNearestOffset(Location(9, 1)))
  }

  @Test
  def maximumIndent(): Unit = {
    assertEquals(0, Lines(0).maximumIndent)
    assertEquals(3, Lines(3).maximumIndent)
    assertEquals(3, Lines(5, List(3)).maximumIndent)
    assertEquals(4, Lines(8, List(3)).maximumIndent)
  }
}

