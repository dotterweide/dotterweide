/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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