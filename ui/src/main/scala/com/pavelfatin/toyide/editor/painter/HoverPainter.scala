/*
 *  HoverPainter.scala
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
import java.io

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.editor.HoverChange
import com.pavelfatin.toyide.node.ReferenceNode

private class HoverPainter(context: PainterContext) extends AbstractPainter(context) with Decorator {
  private val HoverAttributes = Map(
    TextAttribute.FOREGROUND -> Color.BLUE,
    TextAttribute.UNDERLINE -> TextAttribute.UNDERLINE_ON)

  terminal.onChange {
    case HoverChange(from, to) =>
      from.foreach(offset => hoverInterval(offset).foreach(notifyObservers))
      to  .foreach(offset => hoverInterval(offset).foreach(notifyObservers))
    case _ =>
  }

  private def hoverInterval(offset: Int): Option[Interval] = {
    data.structure.flatMap(_.elements.find(node =>
      node.isInstanceOf[ReferenceNode] && node.span.includes(offset))).map(_.span.interval)
  }

  def id = "hover"

  def paint(g: Graphics, bounds: Rectangle): Unit = ()

  override def decorations: Map[Interval, Map[TextAttribute, io.Serializable]] =
    terminal.hover.flatMap(hoverInterval)
      .map(interval => (interval, HoverAttributes)).toMap
}
