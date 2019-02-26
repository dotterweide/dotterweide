/*
 *  FlashAction.scala
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

import java.awt.event.KeyEvent

import dotterweide.Span
import dotterweide.document.Document
import dotterweide.editor.ControllerOps._
import dotterweide.editor.{Action, Flash, Terminal}

import scala.collection.immutable.{Seq => ISeq}

class FlashAction(
                   val document: Document,
                   val terminal: Terminal,
                   val flash   : Flash,
                   val duration: Int           = 500,
                   val level   : Int           = Flash.LevelInfo,
                   val name    : String        = "Flash",
                   val mnemonic: Char          = KeyEvent.VK_UNDEFINED.toChar,
                   val keys    : ISeq[String]  = "shift pressed ENTER" :: Nil
                 )
  extends Action {

  /** Sub-classes may override this. Default implementation does nothing. */
  protected def run(id: Int, span: Span): Unit = ()

  /** Sub-classes may override this. Default implementation returns selection or current line. */
  protected def mkSpan(): Span = {
    val iv = terminal.selection.getOrElse(terminal.currentLineIntervalIn(document))
    Span(document.characters, iv)
  }

  def apply(): Unit = {
    val span  = mkSpan()
    val id    = flash.emit(duration = duration, interval = span.interval, level = level)
    run(id, span)
  }
}