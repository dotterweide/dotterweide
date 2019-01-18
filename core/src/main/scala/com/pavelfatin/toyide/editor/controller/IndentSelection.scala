/*
 *  IndentSelection.scala
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

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{Action, Terminal}
import com.pavelfatin.toyide.Interval

private class IndentSelection(document: Document, terminal: Terminal, tabSize: Int) extends Action with Repeater {
  repeat(document, terminal)

  def keys: Seq[String] = List("pressed TAB")

  override def enabled: Boolean = terminal.selection.isDefined

  def apply(): Unit =
    terminal.selection.foreach { it =>
      val selection = if (document.toLocation(it.end).indent == 0) it.withEndShift(-1) else it
      val beginLine = document.lineNumberOf(selection.begin)
      val endLine = document.lineNumberOf(selection.end)
      val interval = Interval(document.startOffsetOf(beginLine), document.endOffsetOf(endLine))

      val text = document.text(interval)
      val indent = List.fill(tabSize)(' ').mkString
      val replacement = text.split("\n").map(indent + _).mkString("\n")
      document.replace(interval, replacement)

      val increment = replacement.length - text.length
      terminal.offset += increment
      terminal.selection = Some(it.withEndShift(increment))
    }
}