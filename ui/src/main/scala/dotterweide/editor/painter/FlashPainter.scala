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
import java.awt.{Graphics2D, Rectangle, Toolkit}

import dotterweide.Interval
import dotterweide.document.AnchoredInterval
import dotterweide.editor.{Color, Flash, Styling}
import javax.swing.Timer

import scala.annotation.switch

/** Paints an interval background between selection and text for a given duration in milliseconds. */
class FlashPainter(context: PainterContext, emitter: Flash)
  extends AbstractPainter(context) {

  def id: String = "flash"

  def layer: Int = Painter.LayerFlash

  private[this] var anchoredItems = List.empty[AnchoredItem]
  private[this] var items         = List.empty[Item]

  private[this] val timer: Timer = new Timer(50, new ActionListener {
    def actionPerformed(e: ActionEvent): Unit = {
      val now       = System.currentTimeMillis()
      items         = Nil
      val _a        = anchoredItems
      anchoredItems = Nil
      _a.foreach { a =>
        val remove = now > a.stop

        val alpha = if (remove) 0x00 else 0xFF - ((now - a.start).toInt * 0xFF / (a.stop - a.start).toInt)
        val value = (alpha << 24) | a.rgb
        val color = new Color(value, true)
        val item  = new Item(a.interval, color)
        items ::= item

        if (remove) a.dispose()
        else anchoredItems ::= a
      }
      if (items.isEmpty) {
        timer.stop()
      } else {
        items.foreach(it => notifyObservers(it.interval))
        Toolkit.getDefaultToolkit.sync()  // Linux repaint speed issue
      }
    }
  })

  private def rgbOfLevel(level: Int): Int = {
    val name = (level: @switch) match {
      case Flash.LevelInfo          => Styling.FlashInfo
      case Flash.LevelWarn          => Styling.FlashWarn
      case _ /* Flash.LevelError */ => Styling.FlashError
    }
    val color = styling(name)
    color.getRGB & 0xFFFFFF
  }

  emitter.onChange {
    case Flash.Emit(id, duration, interval, level) =>
      val start   = System.currentTimeMillis()
      val stop    = start + duration
      val rgb     = rgbOfLevel(level)
      val a       = new AnchoredItem(id = id, origin = interval, start = start, stop = stop, rgb = rgb)
      anchoredItems ::= a
      timer.restart()

    case Flash.LevelChanged(id, level) =>
      anchoredItems.find(_.id == id).foreach(_.rgb = rgbOfLevel(level))
  }

  override def dispose(): Unit = {
    timer.stop()
    anchoredItems = Nil
  }

  def paint(g: Graphics2D, bounds: Rectangle): Unit =
    if (items.nonEmpty) {
      items.foreach { item =>
        val rectangles = rectanglesOf(item.interval).map(_.intersection(bounds)).filterNot(_.isEmpty)

        if (rectangles.nonEmpty) {
          g.setColor(item.color)
          rectangles.foreach(fill(g, _))
        }
      }
    }

  private class Item(val interval: Interval, val color: Color)

  private class AnchoredItem(val id: Int, origin: Interval, val start: Long, val stop: Long, var rgb: Int)
    extends AnchoredInterval(document, origin)
}