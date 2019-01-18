/*
 *  Canvas.scala
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

import java.awt.{Dimension, Rectangle}

import dotterweide.ObservableEvents

trait Canvas extends ObservableEvents[CanvasEvent] {
  def size: Dimension

  def visible: Boolean

  def visibleRectangle: Rectangle

  def hasFocus: Boolean

  def caretVisible: Boolean
}

sealed trait CanvasEvent

case class VisibilityChanged      (b: Boolean   ) extends CanvasEvent
case class VisibleRectangleChanged(r: Rectangle ) extends CanvasEvent
case class FocusChanged           (b: Boolean   ) extends CanvasEvent
case class CaretVisibilityChanged (b: Boolean   ) extends CanvasEvent
