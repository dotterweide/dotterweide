/*
 *  MockConsole.scala
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

import dotterweide.editor.Color
import dotterweide.ide.Console

import scala.swing.{Component, Label}

class MockConsole extends Console {
  private val builder = new StringBuilder()

  def clear(): Unit = {
    builder.clear()
  }

  def print(s: String): Unit = {
    builder.append(s)
  }

  def print(s: String, color: Color): Unit = {
    print(s)
  }

  def printLink(s: String, line: Int): Unit = {
    print(s)
  }

  lazy val component: Component = new Label

  def text: String = builder.toString()
}