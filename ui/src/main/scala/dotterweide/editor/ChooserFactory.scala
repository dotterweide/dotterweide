/*
 *  ChooserFactory.scala
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

import java.awt.event.{ActionEvent, FocusAdapter, FocusEvent, KeyAdapter, KeyEvent, MouseAdapter, MouseEvent}
import java.awt.{Font, Point, Window}

import javax.swing.{AbstractAction, JComponent, JList, JScrollPane, JWindow, ListCellRenderer, Popup, PopupFactory, SwingUtilities}

import scala.collection.immutable.{Seq => ISeq}

object ChooserFactory {
  def createPopup[A](parent: JComponent, point: Point, font: Font, variants: ISeq[A],
                     renderer: ListCellRenderer[AnyRef])
                    (callback: Option[A] => Unit): (Popup, JList[AnyRef]) = {
    val list = createList(variants, font)

    list.setCellRenderer(renderer)

    val pane    = new JScrollPane(list)
    pane.putClientProperty("styleId", "nofocus")

    val factory = PopupFactory.getSharedInstance
    val shift   = parent.getLocationOnScreen
    val popup   = factory.getPopup(parent, pane, shift.x + point.x, shift.y + point.y)

    def finish(confirm: Boolean): Unit = {
      val res = if (confirm) Option(list.getSelectedValue.asInstanceOf[A]) else None
      callback(res)
      popup.hide()
    }

    list.addFocusListener(new FocusAdapter() {
      override def focusLost(e: FocusEvent): Unit = finish(false)
    })

    list.addKeyListener(new KeyAdapter() {
      override def keyTyped(e: KeyEvent): Unit = {
        val kc = e.getKeyChar
        if      (kc == KeyEvent.VK_ESCAPE ) finish(false)
        else if (kc == KeyEvent.VK_ENTER  ) finish(true )
      }
    })

    list.addMouseListener(new MouseAdapter {
      override def mouseClicked(e: MouseEvent): Unit =
        if (e.getClickCount == 2) finish(true)
    })

    // cf. https://stackoverflow.com/questions/48583428/jtextfield-get-no-focus-when-in-a-heavy-weight-popup
    // solves issue #7
    val win = SwingUtilities.windowForComponent(pane)
    if (win.isInstanceOf[JWindow] && win.getType == Window.Type.POPUP) {
      win.setFocusableWindowState(true)
    }

    (popup, list)
  }

  private def createList(variants: ISeq[Any], font: Font): JList[AnyRef] = {
    val list = new JList(variants.iterator.map(_.asInstanceOf[AnyRef]).toArray)
    list.setFont(font)
    list.setSelectedIndex(0)
    list.setVisibleRowCount(math.min(variants.size, 10))

    val am        = list.getActionMap
    val next      = am.get("selectNextRow")
    val previous  = am.get("selectPreviousRow")
    val first     = am.get("selectFirstRow")
    val last      = am.get("selectLastRow")

    am.put("selectPreviousRow", new AbstractAction() {
      def actionPerformed(e: ActionEvent): Unit = {
        val action = if (list.getSelectedIndex == 0) last else previous
        action.actionPerformed(e)
      }
    })

    am.put("selectNextRow", new AbstractAction() {
      def actionPerformed(e: ActionEvent): Unit = {
        val action = if (list.getSelectedIndex == list.getModel.getSize - 1) first else next
        action.actionPerformed(e)
      }
    })

    list
  }
}