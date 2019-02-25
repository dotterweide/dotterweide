/*
 *  FontSettingsImpl.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */
package dotterweide.editor

class FontSettingsImpl(family0: String, size0: Int, lineSpacing0: Float) extends FontSettings {
  private[this] var _family       = family0
  private[this] var _size         = size0
  private[this] var _lineSpacing  = lineSpacing0

  def family      : String  = _family
  def size        : Int     = _size
  def lineSpacing : Float   = _lineSpacing

  def family_=(value: String): Unit = if (_family != value) {
    _family = value
    notifyObservers()
  }

  def size_=(value: Int): Unit = if (_size != value) {
    _size = value
    notifyObservers()
  }

  def lineSpacing_=(value: Float): Unit = if (_lineSpacing != value) {
    _lineSpacing = value
    notifyObservers()
  }
}