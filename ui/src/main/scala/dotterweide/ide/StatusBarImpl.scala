/*
 *  StatusBarImpl.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.ide

import scala.swing.{Alignment, BoxPanel, Component, Label, Orientation, Swing}

class StatusBarImpl extends BoxPanel(Orientation.Horizontal) with StatusBar {
  def component: Component = this

  private[this] val _message = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  private[this] val _position = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  border = Swing.EmptyBorder(2, 4, 2, 4)
  contents += _position
  contents += Swing.HStrut(8)
  contents += _message

  def message: String = _message.text

  def message_=(s: String): Unit =
    _message.text = s

  def position: String = _position.text

  def position_=(s: String): Unit =
    _position.text = s
}