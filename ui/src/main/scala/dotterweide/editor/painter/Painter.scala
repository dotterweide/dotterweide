/*
 *  Painter.scala
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

import dotterweide.ObservableEvents

/** An object that participates in painting the editor component.
  * If fires "dirty" rectangles that need repainting.
  */
trait Painter extends ObservableEvents[Rectangle] {
  /** Purely informal / informational */
  def id: String

  /** Relative position in the painting stack. Lower layers are painted early,
    * higher layers are painted on top of lower layers.
    */
  def layer: Int

  def immediate: Boolean = false

  /** Paints the contents within the given visual bounds. */
  def paint(g: Graphics2D, bounds: Rectangle): Unit

  def dispose(): Unit
}
object Painter {
  final val LayerImmediate    = -100
  final val LayerBackground   = 0
  final val LayerCurrentLine  = 100
  final val LayerErrors       = 200
  final val LayerMatches      = 300
  final val LayerHighlights   = 400
  final val LayerHover        = 500
  final val LayerSelection    = 600
  final val LayerFlash        = 700
  final val LayerText         = 800
  final val LayerCaret        = 900
}