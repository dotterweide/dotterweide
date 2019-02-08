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

import dotterweide.editor.{CaretMovement, CaretVisibilityChanged, Styling}

// XXX TODO --- support overwrite mode (block cursor)

/** Paints the cursor position as a vertical line. */
private class CaretPainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "caret"

  terminal.onChange {
    case CaretMovement(_, before, now) =>
      notifyObservers(caretRectangleAt(before))
      notifyObservers(caretRectangleAt(now))
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
        g.setColor(styling(Styling.CaretForeground))
        fill(g, rectangle)
      }
    }
}
