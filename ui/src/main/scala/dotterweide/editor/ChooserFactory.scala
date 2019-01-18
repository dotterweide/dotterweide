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

import java.awt.event.{ActionEvent, FocusAdapter, FocusEvent, KeyAdapter, KeyEvent}
import java.awt.{Font, Point, Color => AWTColor}

import javax.swing.border.LineBorder
import javax.swing.{AbstractAction, JComponent, JList, JScrollPane, ListCellRenderer, Popup, PopupFactory}

object ChooserFactory {
  def createPopup[A](parent: JComponent, point: Point, font: Font, variants: Seq[A], renderer: ListCellRenderer[AnyRef])
                    (callback: Option[A] => Unit): (Popup, JList[AnyRef]) = {
    val list = createList(variants, font)

    list.setCellRenderer(renderer)

    val pane = new JScrollPane(list)
    pane.setBorder(new LineBorder(AWTColor.LIGHT_GRAY))

    val factory = PopupFactory.getSharedInstance
    val shift   = parent.getLocationOnScreen
    val popup   = factory.getPopup(parent, pane, shift.x + point.x, shift.y + point.y)

    list.addFocusListener(new FocusAdapter() {
      override def focusLost(e: FocusEvent): Unit = {
        callback(None)
        popup.hide()
      }
    })

    list.addKeyListener(new KeyAdapter() {
      override def keyTyped(e: KeyEvent): Unit = {
        val kc = e.getKeyChar
        if (kc == KeyEvent.VK_ESCAPE) {
          callback(None)
          popup.hide()

        } else if (kc == KeyEvent.VK_ENTER) {
          callback(Some(list.getSelectedValue.asInstanceOf[A]))
          popup.hide()
        }
      }
    })

    (popup, list)
  }

  private def createList(variants: Seq[Any], font: Font) = {
    val list = new JList(variants.iterator.map(_.asInstanceOf[AnyRef]).toArray)
    list.setBackground(new AWTColor(235, 244, 254))
    list.setSelectionBackground(new AWTColor(0, 82, 164))
    list.setFont(font)
    list.setSelectedIndex(0)
    list.setVisibleRowCount(variants.size min 10)

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