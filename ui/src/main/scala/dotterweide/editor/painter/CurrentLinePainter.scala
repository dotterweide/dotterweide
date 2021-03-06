/*
 *  CurrentLinePainter.scala
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

import java.awt.{Graphics2D, Rectangle}

import dotterweide.editor.{CaretMovement, Styling}

/** Simply paints the line in which is the cursor is located using `Coloring.CurrentLineBackground` */
private class CurrentLinePainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "current line"

  def layer: Int = Painter.LayerCurrentLine

  terminal.onChange {
    case CaretMovement(_, before, now) =>
      val fromRectangle = lineRectangleAt(before)
      val toRectangle   = lineRectangleAt(now)

      if (fromRectangle != toRectangle) {
        // XXX TODO --- if rectangles are adjacent, could fire one union rectangle
        notifyObservers(fromRectangle)
        notifyObservers(toRectangle)
      }
    case _ =>
  }

  override def paint(g: Graphics2D, bounds: Rectangle): Unit = {
    val rectangle = lineRectangleAt(terminal.offset).intersection(bounds)

    if (!rectangle.isEmpty) {
      g.setColor(styling(Styling.CurrentLineBackground))
      fill(g, rectangle)
    }
  }
}
