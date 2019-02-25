/*
 *  HighlightPainter.scala
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

import dotterweide.editor.{HighlightsChange, Styling}

private class HighlightPainter(context: PainterContext) extends AbstractPainter(context) {
  def id = "highlight"

  def layer: Int = Painter.LayerHighlights

  terminal.onChange {
    case HighlightsChange(_, before, now) =>
      before.foreach(notifyObservers)
      now   .foreach(notifyObservers)
    case _ =>
  }

  def paint(g: Graphics2D, bounds: Rectangle): Unit = {
    val rectangles = terminal.highlights.flatMap(rectanglesOf)
      .map(_.intersection(bounds)).filterNot(_.isEmpty)

    if (rectangles.nonEmpty) {
      g.setColor(styling(Styling.HighlightBackground))
      rectangles.foreach(fill(g, _))
    }
  }
}
