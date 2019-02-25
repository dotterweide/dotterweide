/*
 *  ErrorPrinter.scala
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

package dotterweide.ide.action

import dotterweide.editor.{Color, Data}
import dotterweide.ide.Console

object ErrorPrinter {
  private[this] val ErrorColor = new Color(127, 0, 0)

  def print(data: Data, console: Console): Unit = {
    console.clear()
    console.print("Errors found:\n", ErrorColor)
    data.errors.filter(_.fatal).foreach { it =>
      // XXX TODO inefficient
      val line = data.text.substring(0, it.interval.start).count(_ == '\n') + 1
      console.print("\nError (", ErrorColor)
      console.printLink(line.toString, line)
      console.print("): %s".format(it.message), ErrorColor)
    }
  }
}