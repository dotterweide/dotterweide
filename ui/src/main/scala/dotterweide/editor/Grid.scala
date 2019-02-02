/*
 *  Grid.scala
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

package dotterweide.editor

import java.awt.{Dimension, Point, Rectangle}

import dotterweide.Observable
import dotterweide.document.Location

/** Grid for the mono-spaced characters of a text.
  * Can translate between cursor positions and visual pixel positions.
  */
trait Grid extends Observable {

  def cellWidth   : Int
  def cellHeight  : Int
  def ascent      : Int

  def insetLeft   : Int
  def insetTop    : Int
  def insetRight  : Int
  def insetBottom : Int

  /** Translates a visible (pixel) point to a virtual text (grid) location.
    * It does so by rounding down the coordinates to the next grid element.
    *
    * Opposite of `toPoint`.
    */
  def toLocation(point: Point): Location

  /** Translates a virtual text (grid) location to a visible (pixel) point.
    *
    * Opposite of `toLocation`.
    */
  def toPoint(location: Location): Point

  /** Translates a visible (pixel) rectangle to a virtual text (grid) area.
    * It does so by rounding down the top and left coordinate, and rounding up the right and bottom
    * coordinate to the next grid element. In other words, the returned area is guaranteed to fully contain
    * the `rectangle` argument.
    *
    * Opposite of `toRectangle`.
    */
  def toArea(rectangle: Rectangle): Area

  /** Translates a virtual text (grid) area to a visible (pixel) rectangle.
    *
    * Opposite of `toArea`.
    */
  def toRectangle(area: Area): Rectangle

  def toSize(lines: Int, maximumIndent: Int): Dimension
}
