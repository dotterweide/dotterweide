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
import java.awt.{Color, Graphics, Graphics2D, Rectangle}

import dotterweide.Interval
import dotterweide.editor.painter.ErrorPainter._
import dotterweide.editor.{Error, ErrorHolder, ErrorsChanged, Styling}
import dotterweide.inspection.Decoration

import scala.collection.immutable.{Seq => ISeq}

/** Paints and decorates errors.
  *
  * It directly paints `Decoration.Fill` (background) and `Decoration.Underline` (wavy),
  * while collecting `Decoration.Red` and `Decoration.Red` as foreground `decorations`.
  */
private class ErrorPainter(context: PainterContext, errors: ErrorHolder)
  extends AbstractPainter(context) with Decorator {

  def id = "errors"

  def layer: Int = Painter.LayerErrors

  errors.onChange {
    case ErrorsChanged(before, after) if canvas.visible =>
      difference(before, after).foreach(notifyObservers)
    case _ =>
  }

  override def paint(g: Graphics2D, bounds: Rectangle): Unit = {
    def relevant(rectangles: ISeq[Rectangle]) =
      rectangles.map(_.intersection(bounds)).filterNot(_.isEmpty)

    val filledRectangles = relevant(rectanglesOf(_ == Decoration.Fill))

    if (filledRectangles.nonEmpty) {
      g.setColor(styling(Styling.FillBackground))
      filledRectangles.foreach(fill(g, _))
    }

    val underlinedRectangles = relevant(rectanglesOf(_ == Decoration.Underline))

    if (underlinedRectangles.nonEmpty) {
      g.setColor(styling(Styling.UnderlineForeground))
      underlinedRectangles.foreach(r => drawWavyLine(g, r.x, r.y + r.height - 2, r.width))
    }
  }

  override def decorations: Map[Interval, Map[TextAttribute, Color]] = {
    val red = intervalsOf(_ == Decoration.Red)
      .map(it => (it, Map(TextAttribute.FOREGROUND -> styling(Styling.RedForeground))))
    val dim = intervalsOf(_ == Decoration.Dim)
      .map(it => (it, Map(TextAttribute.FOREGROUND -> styling(Styling.DimForeground))))
    (red ++ dim).toMap
  }

  /** Filters the errors according to predicate `p`, returning their corresponding visual rectangles */
  private def rectanglesOf(p: Decoration => Boolean): ISeq[Rectangle] =
    intervalsOf(p).flatMap(rectanglesOf)

  /** Filters the errors according to predicate `p`, returning their corresponding intervals */
  private def intervalsOf(p: Decoration => Boolean): ISeq[Interval] =
    errors.errors.iterator.filter(error => p(error.decoration)).map(_.interval).toList
}

private object ErrorPainter {
  /** Calculates the sequence of intervals for errors that appeared or disappeared. */
  private def difference(before: ISeq[Error], after: ISeq[Error]): ISeq[Interval] = {
    val removedErrors = before.filterNot(after  .contains)
    val addedErrors   = after .filterNot(before .contains)
    (removedErrors ++ addedErrors).map(_.interval)
  }

  /** Draws a wavy line in the current color, horizontally from `x` to `x + length`
    * and vertically around `y` (plus, minus one pixel)
    */
  private def drawWavyLine(g: Graphics, x: Int, y: Int, length: Int): Unit = {
    // XXX TODO -- optimize
    val xs      = Range(x, x + length, 2)
    val points  = xs.size
    val ys      = Stream.continually(()).flatMap(_ => Seq(y + 1, y - 1)).take(points)
    g.drawPolyline(xs.toArray, ys.toArray, points)
  }
}

