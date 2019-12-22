/*
 *  Editor.scala
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

import dotterweide.{Observable, Platform}
import dotterweide.document.Document
import dotterweide.editor.painter.{Painter, PainterContext}

import scala.swing.Component

trait Editor extends Observable {
  def document: Document

  def data: Data

  def terminal: Terminal

  def errorHolder: ErrorHolder

  implicit def async: Async

  implicit def platform: Platform

  /** The main component enclosing all sub-components represented by the editor. */
  def component: Component

  /** The text editor component which should get focus while editing. */
  def pane: Component

  var text: String

  def actions: EditorActions

  def message: Option[String]

  /** Disposes all resources associated with the editor. */
  def dispose(): Unit

  def painterContext: PainterContext

  /** Adds a custom painter and inserts it at its layer position. */
  def addPainter(p: Painter): Unit

  /** Removes a custom painter. */
  def removePainter(p: Painter): Unit

  /** Registers a custom action. */
  def addAction(a: Action): Unit

  /** Unregisters a custom action. */
  def removeAction(a: Action): Unit
}