/*
 *  ObservableEventsTest.scala
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

package dotterweide

import org.junit.Test
import org.junit.Assert._

class ObservableEventsTest {
  @Test
  def singleNotification(): Unit = {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    observable.onChange(events ::= _)
    observable.notifyObservers("foo")
    assertEquals(List("foo"), events.reverse)
  }

  @Test
  def multipleNotifications(): Unit = {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    observable.onChange(events ::= _)
    observable.notifyObservers("foo")
    observable.notifyObservers("bar")
    assertEquals(List("foo", "bar"), events.reverse)
  }

  @Test
  def disconnection(): Unit = {
    val observable = new ObservableEvents[String]() {}
    var events = List[String]()
    val recorder = events ::= (_: String)
    observable.onChange(recorder)
    observable.notifyObservers("foo")
    observable.disconnect(recorder)
    observable.notifyObservers("bar")
    assertEquals(List("foo"), events.reverse)
  }
}