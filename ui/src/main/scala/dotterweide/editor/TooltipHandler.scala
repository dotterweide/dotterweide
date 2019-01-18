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
import java.awt.{Dimension, Graphics, Point, Color => AWTColor}

import javax.swing.border.{CompoundBorder, EmptyBorder, LineBorder}
import javax.swing.{JComponent, JLabel, Popup, PopupFactory, Timer}

private class TooltipHandler(component: JComponent, lookup: Point => Option[Error]) {
  private val Timeout           = 500
  private val TooltipShift      = new Dimension(5, 5)
  private val TooltipBackground = new AWTColor(0xFDFEE2)

  private val TooltipBorder = new CompoundBorder(
    new LineBorder(AWTColor.BLACK, 1, true),
    new EmptyBorder(3, 3, 3, 3))

  private var pointer : Option[Point] = None
  private var popup   : Option[Popup] = None

  private val tooltipTimer = new Timer(Timeout, new ActionListener() {
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

      for (point <- pointer; if p.distance(point.x, point.y) < 7)
        return

      popup.foreach(it => it.hide())
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
    label.setBorder(TooltipBorder)
    factory.getPopup(component, label, shift.x + point.x, shift.y + point.y)
  }

  private def createLabel(error: Error): JLabel =
    new JLabel(error.message) {
      override def paint(g: Graphics): Unit = {
        g.setColor(TooltipBackground)
        g.fillRect(0, 0, getWidth, getHeight)
        super.paint(g)
      }
    }

  def dispose(): Unit =
    tooltipTimer.stop()
}