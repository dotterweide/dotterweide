/*
 *  Console.scala
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

import dotterweide.Output
import dotterweide.editor.Color

trait Console extends Output {
  def print(s: String, color: Color): Unit

  def printLink(s: String, line: Int): Unit

  def clear(): Unit
}

object Console {
  object Null extends Console {
    def print(s: String): Unit = ()

    def print(s: String, color: Color): Unit = ()

    def printLink(s: String, line: Int): Unit = ()

    def clear(): Unit = ()
  }
}