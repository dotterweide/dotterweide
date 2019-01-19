/*
 *  ErrorPainter.scala
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

import java.awt.font.TextAttribute
import java.awt.{Color, Graphics, Rectangle}

import dotterweide.Interval
import dotterweide.editor.painter.ErrorPainter._
import dotterweide.editor.{Coloring, Error, ErrorHolder, ErrorsChanged}
import dotterweide.inspection.Decoration

import scala.collection.immutable.{Seq => ISeq}

private class ErrorPainter(context: PainterContext, errors: ErrorHolder) extends AbstractPainter(context) with Decorator {
  def id = "errors"

  errors.onChange {
    case ErrorsChanged(before, after) if canvas.visible =>
      difference(before, after).foreach(notifyObservers)
    case _ =>
  }

  override def paint(g: Graphics, bounds: Rectangle): Unit = {
    def relevant(rectangles: ISeq[Rectangle]) =
      rectangles.map(_.intersection(bounds)).filterNot(_.isEmpty)

    val filledRectangles = relevant(rectanglesOf(_ == Decoration.Fill))

    if (filledRectangles.nonEmpty) {
      g.setColor(coloring(Coloring.FillBackground))
      filledRectangles.foreach(fill(g, _))
    }

    val underlinedRectangles = relevant(rectanglesOf(_ == Decoration.Underline))

    if (underlinedRectangles.nonEmpty) {
      g.setColor(coloring(Coloring.UnderlineForeground))
      underlinedRectangles.foreach(r => drawWavyLine(g, r.x, r.y + r.height - 2, r.width))
    }
  }

  override def decorations: Map[Interval, Map[TextAttribute, Color]] =
    (intervalsOf(_ == Decoration.Red)
      .map(it => (it, Map(TextAttribute.FOREGROUND -> coloring(Coloring.RedForeground)))) ++
      intervalsOf(_ == Decoration.Dim)
        .map(it => (it, Map(TextAttribute.FOREGROUND -> coloring(Coloring.DimForeground))))).toMap

  private def rectanglesOf(p: Decoration => Boolean): ISeq[Rectangle] =
    intervalsOf(p).flatMap(rectanglesOf)

  private def intervalsOf(p: Decoration => Boolean): ISeq[Interval] =
    errors.errors.iterator.filter(error => p(error.decoration)).map(_.interval).toList
}

private object ErrorPainter {
  private def difference(before: ISeq[Error], after: ISeq[Error]): ISeq[Interval] = {
    val removedErrors = before.filterNot(mark => after  .contains(mark))
    val addedErrors   = after .filterNot(mark => before .contains(mark))
    (removedErrors ++ addedErrors).map(_.interval)
  }

  private def drawWavyLine(g: Graphics, x: Int, y: Int, length: Int): Unit = {
    val xs      = Range(x, x + length, 2)
    val points  = xs.size
    val ys      = Stream.continually(()).flatMap(_ => Seq(y + 1, y - 1)).take(points)
    g.drawPolyline(xs.toArray, ys.toArray, points)
  }
}
