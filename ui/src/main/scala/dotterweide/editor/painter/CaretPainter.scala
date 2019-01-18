/*
 *  CaretPainter.scala
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

package dotterweide.editor.painter

import java.awt.{Graphics, Rectangle}

import dotterweide.editor.{CaretMovement, CaretVisibilityChanged, Coloring}

private class CaretPainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "caret"

  terminal.onChange {
    case CaretMovement(from, to) =>
      notifyObservers(caretRectangleAt(from))
      notifyObservers(caretRectangleAt(to))
    case _ =>
  }

  canvas.onChange {
    case CaretVisibilityChanged(_) =>
      notifyObservers(caretRectangleAt(terminal.offset))
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle): Unit =
    if (canvas.caretVisible) {
      val rectangle = caretRectangleAt(terminal.offset).intersection(bounds)

      if (!rectangle.isEmpty) {
        g.setColor(coloring(Coloring.CaretForeground))
        fill(g, rectangle)
      }
    }
}
