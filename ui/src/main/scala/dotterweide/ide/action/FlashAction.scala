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

package dotterweide.ide.action

import dotterweide.Span
import dotterweide.editor.Flash
import dotterweide.ide.Panel
import javax.swing.KeyStroke

import scala.swing.{Action, Component}
import dotterweide.editor.ControllerOps._

class FlashAction(title0: String, mnemonic0: Char, shortcut: String, parent: Component, panel: Panel,
                  flash: Flash, duration: Int, level: Int)
  extends Action(title0) {

  mnemonic = mnemonic0

  accelerator = Some(KeyStroke.getKeyStroke(shortcut))

  /** Sub-classes may override this. Default implementation does nothing. */
  protected def run(id: Int, span: Span): Unit = ()

  /** Sub-classes may override this. Default implementation returns selection or current line. */
  protected def mkSpan(): Span = {
    val e   = panel.currentEditor
    val d   = e.document
    val iv  = e.terminal.selection.getOrElse(e.terminal.currentLineIntervalIn(d))
    val txt = d.text(iv)
    Span(txt, iv)
  }

  def apply(): Unit = {
    val span  = mkSpan()
    val id    = flash.emit(duration = duration, interval = span.interval, level = level)
    run(id, span)
  }
}