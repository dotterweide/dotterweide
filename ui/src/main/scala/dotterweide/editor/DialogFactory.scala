/*
 *  DialogFactory.scala
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

import java.awt.event.{ActionEvent, KeyEvent, WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, FlowLayout, Frame}

import javax.swing.border.EmptyBorder
import javax.swing.{AbstractAction, Box, JButton, JComponent, JDialog, JPanel, JTextField, KeyStroke, WindowConstants}

object DialogFactory {
  def create(owner: JComponent, text: String, title: String)(callback: Option[String] => Unit): JDialog = {
    val dialog = new JDialog(owner.getTopLevelAncestor.asInstanceOf[Frame], title)

    val field = new JTextField(text)
    field.setColumns(20)
    field.setCaretPosition(text.length)
    field.selectAll()

    var done = false

    object OkAction extends AbstractAction("OK") {
      def actionPerformed(e: ActionEvent): Unit = {
        done = true
        dialog.dispose()
        callback(Some(field.getText))
      }
    }

    object CancelAction extends AbstractAction("Cancel") {
      def actionPerformed(e: ActionEvent): Unit = {
        done = true
        dialog.dispose()
        callback(None)
      }
    }

    val ok      = new JButton(OkAction)
    val cancel  = new JButton(CancelAction)

    val buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0))
    buttons.add(ok)
    buttons.add(Box.createHorizontalStrut(5))
    buttons.add(cancel)
    ok.setPreferredSize(cancel.getPreferredSize)

    val content = new JPanel(new BorderLayout(0, 3))
    content.setBorder(new EmptyBorder(10, 10, 10, 10))
    content.add(field, BorderLayout.NORTH)
    content.add(buttons, BorderLayout.SOUTH)

    dialog.getRootPane.registerKeyboardAction(OkAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)

    dialog.getRootPane.registerKeyboardAction(CancelAction,
      KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW)

    dialog.getRootPane.setDefaultButton(ok)
    dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    dialog.setContentPane(content)

    dialog.addWindowListener(new WindowAdapter() {
      override def windowClosed(e: WindowEvent): Unit = {
        if (!done) {
          done = true
          callback(None)
        }
      }
    })

    dialog
  }
}