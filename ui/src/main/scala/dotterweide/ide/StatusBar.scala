/*
 *  StatusBar.scala
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

package dotterweide.ide

import scala.swing.{Alignment, FlowPanel, Label}

class StatusBar extends FlowPanel(FlowPanel.Alignment.Left)() {
  private[this] val _message = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  private[this] val _position = new Label() {
    horizontalAlignment = Alignment.Leading
  }

  contents += _position
  contents += _message

  def message: String = _message.text

  def message_=(s: String): Unit =
    _message.text = s

  def position: String = _position.text

  def position_=(s: String): Unit =
    _position.text = s
}