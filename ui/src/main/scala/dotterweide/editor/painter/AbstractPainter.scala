/*
 *  AbstractPainter.scala
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

import java.awt.{Graphics, Point, Rectangle}

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.editor.{Area, Canvas, Data, FontSettings, Grid, Styling, Terminal}

import scala.collection.immutable.{Seq => ISeq}

private abstract class AbstractPainter(context: PainterContext) extends Painter {
  protected def document: Document      = context.document
  protected def terminal: Terminal      = context.terminal
  protected def data    : Data          = context.data
  protected def canvas  : Canvas        = context.canvas
  protected def grid    : Grid          = context.grid
  protected def styling : Styling       = context.styling
  protected def font    : FontSettings  = context.font

  protected def contains(chars: CharSequence, char: Char): Boolean =
    Range(0, chars.length).exists(i => chars.charAt(i) == char)

  /** Fills a rectangle on the graphics using its current color */
  protected def fill(g: Graphics, r: Rectangle): Unit =
    g.fillRect(r.x, r.y, r.width, r.height)

  /** Calculates the visual location of a text offset */
  protected def toPoint(offset: Int): Point =
    grid.toPoint(document.toLocation(offset))

  /** Calculates the visual rectangles for a text interval, and publishes events for them.
    *
    * @param  interval  a "dirty" interval that needs repainting
    */
  protected def notifyObservers(interval: Interval): Unit =
    rectanglesOf(interval).foreach(notifyObservers)

  /** Calculates the visual rectangle for a text line containing an offset */
  protected def lineRectangleAt(offset: Int): Rectangle = {
    val point = toPoint(offset)
    new Rectangle(0, point.y, canvas.size.width, grid.cellHeight)
  }

  /** Calculates the caret's visual rectangle given a text offset */
  protected def caretRectangleAt(offset: Int): Rectangle = {
    val point = toPoint(offset)
    new Rectangle(point.x, point.y, 2, grid.cellHeight)
  }

  /** Calculates the visual rectangles covering a given text interval */
  protected def rectanglesOf(interval: Interval): ISeq[Rectangle] = {
    val width   = canvas.size.width
    val height  = grid.cellHeight

    val p1      = toPoint(interval.begin)
    val p2      = toPoint(interval.end  )

    if (p1.y == p2.y) {
      new Rectangle(p1.x, p1.y, p2.x - p1.x, height) :: Nil
    } else {
      val left  = grid.insetLeft
      val p1bot = p1.y + height
      List(
        new Rectangle(p1.x, p1.y  , width - p1.x, height),
        new Rectangle(left, p1bot , width - left, p2.y - p1bot),
        new Rectangle(left, p2.y  , p2.x  - left, height))
    }
  }

  /** Translates a text area to an interval.
    * The interval is enlarged to start at the beginning
    * of a line and to stop at the end of a line.
    */
  protected def intervalOf(area: Area): Interval = {
    val beginLine = bound(area.line)
    val endLine   = bound(area.line + area.height)
    Interval(document.startOffsetOf(beginLine), document.endOffsetOf(endLine))
  }

  /** Clips a line index to the valid range of line indices */
  private def bound(line: Int): Int =
    math.max(0, math.min(line, document.linesCount - 1))
}
