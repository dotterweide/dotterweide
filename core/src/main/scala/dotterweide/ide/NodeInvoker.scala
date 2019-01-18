/*
 *  NodeInvoker.scala
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

import dotterweide.compiler.{Assembler, BytecodeInvoker, InvocationException, TranslationException}
import dotterweide.editor.{Color, Runner}
import dotterweide.node.Node

private class NodeInvoker(console: Console) extends Runner {
  private val ErrorColor = new Color(127, 0, 0)

  private val Name = "Main"

  def run(root: Node): Unit = {
    console.clear()
    try {
      val bytecode = Assembler.assemble(root, Name)
      console.print("Started:\n")
      val before = System.currentTimeMillis
      try {
        BytecodeInvoker.invoke(bytecode, Name, console)
        val elapsed = System.currentTimeMillis - before
        console.print("\nFinished (%d ms)".format(elapsed))
      } catch {
        case InvocationException(message, trace) =>
          if (message != "java.lang.ThreadDeath") {
            console.print("\n%s".format(message), ErrorColor)
            trace.foreach { place =>
              val line = place.line + 1
              console.print("\n  at ", ErrorColor)
              console.printLink("%s%d".format(place.enclosure.map(_.formatted("%s:")).mkString, line), line)
            }
          }
      }
    } catch {
      case TranslationException(message) => console.print("Compilation error.\n%s".format(message), ErrorColor)
    }
  }
}
