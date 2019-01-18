/*
 *  IntervalTest.scala
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

package com.pavelfatin.toyide

import org.junit.Test
import org.junit.Assert._

class IntervalTest {
  @Test(expected = classOf[IllegalArgumentException])
  def negativeBegin(): Unit = {
    Interval(-1, 0)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def negativeEnd(): Unit = {
    Interval(0, -1)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def negativeLength(): Unit = {
    Interval(1, 0)
  }

  @Test
  def length(): Unit = {
    assertEquals(0, Interval(3, 3).length)
    assertEquals(1, Interval(3, 4).length)
    assertEquals(2, Interval(3, 5).length)
    assertEquals(3, Interval(3, 6).length)
  }

  @Test
  def empty(): Unit = {
    assertTrue(Interval(3, 3).empty)
    assertFalse(Interval(3, 4).empty)
    assertFalse(Interval(3, 5).empty)
    assertFalse( Interval(3, 6).empty)
  }

  @Test
  def includes(): Unit = {
    assertFalse(Interval(5, 5).includes(5))

    assertFalse(Interval(5, 6).includes(4))
    assertTrue(Interval(5, 6).includes(5))
    assertFalse(Interval(5, 6).includes(6))

    assertFalse(Interval(5, 7).includes(4))
    assertTrue(Interval(5, 7).includes(5))
    assertTrue(Interval(5, 7).includes(6))
    assertFalse(Interval(5, 7).includes(7))

    assertFalse(Interval(5, 8).includes(4))
    assertTrue(Interval(5, 8).includes(5))
    assertTrue(Interval(5, 8).includes(6))
    assertTrue(Interval(5, 8).includes(7))
    assertFalse(Interval(5, 8).includes(8))
  }

  @Test
  def includesInterval(): Unit = {
    assertFalse(Interval(5, 5).includes(Interval(5, 5)))
    assertFalse(Interval(5, 7).includes(Interval(6, 6)))
    assertFalse(Interval(5, 5).includes(Interval(5, 7)))

    assertFalse(Interval(5, 7).includes(Interval(4, 5)))
    assertTrue(Interval(5, 7).includes(Interval(5, 6)))

    assertFalse(Interval(5, 7).includes(Interval(7, 8)))
    assertTrue(Interval(5, 7).includes(Interval(6, 7)))

    assertTrue(Interval(5, 7).includes(Interval(5, 7)))
    assertTrue(Interval(5, 7).includes(Interval(5, 6)))
    assertTrue(Interval(5, 7).includes(Interval(6, 7)))

    assertFalse(Interval(5, 7).includes(Interval(4, 7)))
    assertFalse(Interval(5, 7).includes(Interval(5, 8)))
    assertFalse(Interval(5, 7).includes(Interval(4, 8)))
  }

  @Test
  def intersectsWith(): Unit = {
    assertNotIntersect(Interval(5, 5), Interval(5, 5))
    assertNotIntersect(Interval(4, 4), Interval(5, 5))
    assertNotIntersect(Interval(6, 6), Interval(5, 5))

    assertNotIntersect(Interval(5, 5), Interval(3, 7))

    assertNotIntersect(Interval(3, 3), Interval(3, 7))
    assertNotIntersect(Interval(6, 6), Interval(3, 7))

    assertNotIntersect(Interval(0, 3), Interval(3, 7))
    assertNotIntersect(Interval(7, 10), Interval(3, 7))

    assertIntersect(Interval(3, 7), Interval(3, 7))

    assertIntersect(Interval(0, 4), Interval(3, 7))
    assertIntersect(Interval(6, 9), Interval(3, 7))

    assertIntersect(Interval(0, 5), Interval(3, 7))
    assertIntersect(Interval(5, 9), Interval(3, 7))

    assertIntersect(Interval(0, 9), Interval(3, 7))
    assertIntersect(Interval(4, 6), Interval(3, 7))
    assertIntersect(Interval(3, 4), Interval(3, 7))
    assertIntersect(Interval(6, 7), Interval(3, 7))

    assertIntersect(Interval(4, 5), Interval(3, 7))
  }

  protected def assertIntersect(a: Interval, b: Interval): Unit = {
    assertTrue(a.intersectsWith(b))
    assertTrue(b.intersectsWith(a))
  }

  protected def assertNotIntersect(a: Interval, b: Interval): Unit = {
    assertFalse(a.intersectsWith(b))
    assertFalse(b.intersectsWith(a))
  }
}