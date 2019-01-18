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

package com.pavelfatin.toyide.ide

import java.awt.{Color, Font}
import javax.swing.text.{AttributeSet, SimpleAttributeSet, StyleConstants}
import javax.swing.{JTextPane, SwingUtilities}

import com.pavelfatin.toyide.editor.Coloring

private class ConsoleImpl(coloring: Coloring) extends JTextPane with Console {
  private val LinkColor = new Color(125, 121, 111)

  setFont(new Font(coloring.fontFamily, Font.PLAIN, coloring.fontSize))
  setEditable(false)

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
    SwingUtilities.invokeLater(new Runnable {
      def run(): Unit =
        getDocument.insertString(getDocument.getLength, s, attributes)
    })

  def clear(): Unit =
    setText("")
}