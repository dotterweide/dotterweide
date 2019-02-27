/*
 *  TooltipHandler.scala
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

import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}
import java.awt.{Dimension, Point}

import javax.swing.{JComponent, JToolTip, Popup, PopupFactory, Timer}

private class TooltipHandler(component: JComponent, lookup: Point => Option[Error]) {
  private[this] val Timeout       = 500
  private[this] val TooltipShift  = new Dimension(5, 5)

  private[this] var pointer       = Option.empty[Point]
  private[this] var popup         = Option.empty[Popup]

  private[this] val tooltipTimer = new Timer(Timeout, new ActionListener() {
    def actionPerformed(e: ActionEvent): Unit =
      for (point <- pointer; error <- lookup(point)) {
        val p = createPopup(error, new Point(point.x + TooltipShift.width, point.y + TooltipShift.height))
        popup = Some(p)
        p.show()
      }
  })

  tooltipTimer.setRepeats(false)

  component.addMouseMotionListener(new MouseAdapter() {
    override def mouseMoved(e: MouseEvent): Unit = {
      val p = e.getPoint

      if (pointer.exists(point => p.distance(point.x, point.y) < 7)) return

      popup.foreach(_.hide())
      pointer = Some(p)
      tooltipTimer.restart()
    }
  })

  component.addMouseListener(new MouseAdapter() {
    override def mouseExited(e: MouseEvent): Unit =
      tooltipTimer.stop()
  })

  private def createPopup(error: Error, point: Point): Popup = {
    val factory = PopupFactory.getSharedInstance
    val shift   = component.getLocationOnScreen
    val label   = createLabel(error)
    factory.getPopup(component, label, shift.x + point.x, shift.y + point.y)
  }

  private def createLabel(error: Error): JComponent = {
    val res = new JToolTip
    res.setTipText(error.message)
    res
  }

  def dispose(): Unit =
    tooltipTimer.stop()
}