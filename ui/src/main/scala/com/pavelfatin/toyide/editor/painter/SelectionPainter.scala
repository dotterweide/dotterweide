/*
 *  SelectionPainter.scala
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

package com.pavelfatin.toyide.editor.painter

import java.awt.font.TextAttribute
import java.awt.{Color, Graphics, Rectangle}

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.editor.{Coloring, SelectionChange}

private class SelectionPainter(context: PainterContext) extends AbstractPainter(context) with Decorator {
  def id = "selection"

  terminal.onChange {
    case SelectionChange(from, to) =>
      from.foreach(notifyObservers)
      to.foreach(notifyObservers)
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle): Unit = {
    val rectangles = terminal.selection.toSeq.flatMap(rectanglesOf)
      .map(_.intersection(bounds)).filterNot(_.isEmpty)

    if (rectangles.nonEmpty) {
      g.setColor(coloring(Coloring.SelectionBackground))
      rectangles.foreach(fill(g, _))
    }
  }

  override def decorations: Map[Interval, Map[TextAttribute, Color]] = terminal.selection
    .map(interval => (interval, Map(TextAttribute.FOREGROUND -> coloring(Coloring.SelectionForeground)))).toMap
}
