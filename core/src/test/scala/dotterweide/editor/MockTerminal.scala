/*
 *  MockTerminal.scala
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

import dotterweide.Interval

class MockTerminal(offset0: Int, selection0: Option[Interval]) extends AbstractTerminal {
  offset = offset0

  selection = selection0

  def choose[A](variants: Seq[A], query: String)(callback: A => Unit): Unit = ()

  def edit(s: String, title: String)(callback: Option[String] => Unit): Unit = ()
}