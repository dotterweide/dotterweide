/*
 *  ShowUsages.scala
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

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.editor.{Action, Terminal, Data}

private class ShowUsages(terminal: Terminal, data: Data) extends Action {
  def keys: Seq[String] = List("shift ctrl pressed F7")

  def apply(): Unit = {
    data.compute()
    terminal.highlights = data.connectedLeafsFor(terminal.offset).map(_.span.interval)
  }
}