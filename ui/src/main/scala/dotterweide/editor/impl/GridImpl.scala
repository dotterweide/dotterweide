/*
 *  GridImpl.scala
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

package dotterweide.editor.impl

import java.awt.{Dimension, Point, Rectangle}

import dotterweide.document.Location
import dotterweide.editor.{Area, Grid}

import scala.math.{ceil, floor, max}

/** Grid for the mono-spaced characters of a text.
  * Can translate between cursor positions and visual pixel positions.
  */
class GridImpl(cellWidth0: Int, cellHeight0: Int, ascent0: Int,
               val insetLeft: Int = 0, val insetTop: Int = 0, val insetRight: Int = 0, val insetBottom: Int = 0)
  extends Grid {

  private[this] var _cellWidth  = cellWidth0
  private[this] var _cellHeight = cellHeight0
  private[this] var _ascent     = ascent0

  def cellWidth : Int = _cellWidth
  def cellHeight: Int = _cellHeight
  def ascent    : Int = _ascent

  def cellWidth_=(value: Int): Unit = if (_cellWidth != value) {
    _cellWidth = value
    notifyObservers()
  }

  def cellHeight_=(value: Int): Unit = if (_cellHeight != value) {
    _cellHeight = value
    notifyObservers()
  }

  def ascent_=(value: Int): Unit = if (_ascent != value) {
    _ascent = value
    notifyObservers()
  }

  /** Translates a visible (pixel) point to a virtual text (grid) location.
    * It does so by rounding down the coordinates to the next grid element.
    *
    * Opposite of `toPoint`.
    */
  def toLocation(point: Point): Location = {
    val line    = floor((point.y - insetTop ).toDouble / cellHeight.toDouble).toInt
    val indent  = floor((point.x - insetLeft).toDouble / cellWidth .toDouble).toInt
    Location(max(0, line), max(0, indent))
  }

  /** Translates a virtual text (grid) location to a visible (pixel) point.
    *
    * Opposite of `toLocation`.
    */
  def toPoint(location: Location): Point =
    new Point(insetLeft + cellWidth * location.indent,
      insetTop + cellHeight * location.line)

  /** Translates a visible (pixel) rectangle to a virtual text (grid) area.
    * It does so by rounding down the top and left coordinate, and rounding up the right and bottom
    * coordinate to the next grid element. In other words, the returned area is guaranteed to fully contain
    * the `rectangle` argument.
    *
    * Opposite of `toRectangle`.
    */
  def toArea(rectangle: Rectangle): Area = {
    val beginLine   = max(0, floor((rectangle.y - insetTop ).toDouble / cellHeight.toDouble).toInt)
    val beginIndent = max(0, floor((rectangle.x - insetLeft).toDouble / cellWidth .toDouble).toInt)

    val endLine   = ceil((rectangle.y - insetTop  + rectangle.height).toDouble / cellHeight.toDouble).toInt
    val endIndent = ceil((rectangle.x - insetLeft + rectangle.width ).toDouble / cellWidth .toDouble).toInt

    Area(beginLine, beginIndent, endIndent - beginIndent, endLine - beginLine)
  }

  /** Translates a virtual text (grid) area to a visible (pixel) rectangle.
    *
    * Opposite of `toArea`.
    */
  def toRectangle(area: Area): Rectangle = {
    val point = toPoint(Location(area.line, area.indent))
    new Rectangle(point.x, point.y, cellWidth * area.width, cellHeight * area.height)
  }

  def toSize(lines: Int, maximumIndent: Int): Dimension = {
    val edge = toPoint(Location(lines, maximumIndent))
    new Dimension(edge.x + cellWidth + insetRight, edge.y + insetBottom)
  }
}
