/*
 *  NodeInterpreter.scala
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

import java.awt.Color

import com.pavelfatin.toyide.editor.Runner
import com.pavelfatin.toyide.interpreter.{ContextImpl, EvaluationException}
import com.pavelfatin.toyide.node.Node

private class NodeInterpreter(console: Console) extends Runner {
  private val ErrorColor = new Color(127, 0, 0)

  def run(root: Node): Unit = {
    console.clear()
    console.print("Started:\n")
    val before = System.currentTimeMillis
    try {
      root.evaluate(new ContextImpl(), console)
      val elapsed = System.currentTimeMillis - before
      console.print("\nFinished (%d ms)".format(elapsed))
    } catch {
      case EvaluationException(message, trace) =>
        console.print("\nError: %s".format(message), ErrorColor)
        trace.foreach { place =>
          val line = place.line + 1
          console.print("\n  at ", ErrorColor)
          console.printLink("%s%d".format(place.enclosure.map(_.formatted("%s:")).mkString, line), line)
        }
    }
  }
}
