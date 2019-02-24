/*
 *  Panel.scala
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

import java.io.File

import dotterweide.ObservableEvents
import dotterweide.editor.{Async, Data, Editor}

import scala.swing.Component

object Panel {
  sealed trait Update
  case class EditorFocused(editor: Editor) extends Update
  case class FileOrDirtyChange(file: Option[File], dirty: Boolean) extends Update
}
trait Panel extends ObservableEvents[Panel.Update] {
  def component: Component

  def console: Console

  def currentEditor: Editor

  def data: Data

  implicit def async: Async

  def status: StatusBar

  def editorTab: EditorTab

  def styling: DynamicStyling

  def dispose(): Unit
}
