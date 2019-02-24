/*
 *  ConsoleImpl.scala
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

package dotterweide.ide

import java.awt.{Color, EventQueue, Font}

import dotterweide.editor.FontSettings
import javax.swing.text.{AttributeSet, SimpleAttributeSet, StyleConstants}

import scala.swing.{Component, Swing, TextPane}

private class ConsoleImpl(_font: FontSettings) extends TextPane with Console {
  private val LinkColor = new Color(125, 121, 111)

  font      = new Font(_font.family, Font.PLAIN, _font.size)
  editable  = false

  def component: Component = this

  def print(s: String): Unit =
    doPrint(s, null)

  def print(s: String, color: Color): Unit = {
    val attributes = new SimpleAttributeSet()
    StyleConstants.setForeground(attributes, color)
    doPrint(s, attributes)
  }

  def printLink(s: String, line: Int): Unit = {
    val attributes = new SimpleAttributeSet()
    StyleConstants.setForeground(attributes, LinkColor)
    doPrint(s, attributes)
  }

  private def doPrint(s: String, attributes: AttributeSet): Unit =
    if (EventQueue.isDispatchThread) {
      val doc = peer.getDocument
      doc.insertString(doc.getLength, s, attributes)
    } else {
      Swing.onEDT(doPrint(s, attributes))
    }

  def clear(): Unit =
    text = ""
}