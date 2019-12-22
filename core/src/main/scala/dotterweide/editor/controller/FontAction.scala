/*
 *  FontAction.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor.controller

import dotterweide.Platform
import dotterweide.editor.{Action, FontSettings}

import scala.collection.immutable.{Seq => ISeq}

private trait FontAction extends Action {
  protected def font: FontSettings
}

private class FontEnlarge(val font: FontSettings)(implicit p: Platform) extends FontAction {
  def name    : String        = "Enlarge Font"
  def mnemonic: Char          = 'E'
  val keys    : ISeq[String]  =  // cf. https://stackoverflow.com/questions/15605109/java-keybinding-plus-key
    s"${p.menuModifier} pressed PLUS" :: s"shift ${p.menuModifier} pressed EQUALS" :: Nil

  def apply(): Unit = {
    val sz = font.size
    font.size = math.max(sz + 1, math.round(sz * 1.1f))
  }
}

private class FontShrink(val font: FontSettings)(implicit p: Platform) extends FontAction {
  def name    : String        = "Shrink Font"
  def mnemonic: Char          = 'S'
  val keys    : ISeq[String]  = s"${p.menuModifier} pressed MINUS" :: Nil

  def apply(): Unit = {
    val sz = font.size
    font.size = math.max(4, math.min(sz - 1, math.round(sz / 1.1f)))
  }
}

private class FontReset(val font: FontSettings)(implicit p: Platform) extends FontAction {
  def name: String        = "Reset Font Size"
  def mnemonic: Char      = 'R'
  val keys: ISeq[String]  = s"${p.menuModifier} pressed 0" :: Nil

  private[this] val defaultSize = font.size

  def apply(): Unit = {
    font.size = defaultSize
  }
}
