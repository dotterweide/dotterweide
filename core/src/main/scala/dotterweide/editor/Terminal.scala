/*
 *  Terminal.scala
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

package dotterweide.editor

import dotterweide.{Interval, ObservableEvents}

import scala.collection.immutable.{Seq => ISeq}

trait Terminal extends ObservableEvents[TerminalEvent] {
  var offset: Int

  var selection: Option[Interval]

  var hover: Option[Int]

  var highlights: ISeq[Interval]

  var overwriteMode: Boolean

  def choose[A](variants: ISeq[A], query: String)(callback: A => Unit): Unit

  def edit(s: String, title: String)(callback: Option[String] => Unit): Unit
}