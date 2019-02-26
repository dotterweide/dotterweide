/*
 *  FlashPainter.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor.painter

import java.awt.event.{ActionEvent, ActionListener}
import java.awt.{Graphics2D, Rectangle}

import dotterweide.editor.Flash
import javax.swing.Timer

/** Paints an interval background between selection and text for a given duration in milliseconds. */
class FlashPainter(context: PainterContext, duration: Int, emitter: Flash)
  extends AbstractPainter(context) {

  def id: String = "flash"

  def layer: Int = Painter.LayerFlash

  private[this] val timer = new Timer(50, new ActionListener {
    def actionPerformed(e: ActionEvent): Unit = ???
  })

  emitter.onChange { e =>
    ???
  }

  override def dispose(): Unit = {
    timer.stop()
  }

  def paint(g: Graphics2D, bounds: Rectangle): Unit = {
    ???
  }
}