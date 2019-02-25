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

import dotterweide.{FileType, ObservableEvents}
import dotterweide.editor.painter.Painter
import dotterweide.editor.{Action, Async, Data, Editor, History}

import scala.swing.Component

object Panel {
  sealed trait Update
  case class EditorFocused(editor : Editor      ) extends Update
  case class FileChanged  (newFile: Option[File]) extends Update
  case class SplitChanged (isSplit: Boolean     ) extends Update
  case class DirtyChanged (isDirty: Boolean     ) extends Update
}
/** The main IDE component. */
trait Panel extends ObservableEvents[Panel.Update] {
  def component: Component

  def currentEditor: Editor

  def data: Data

  implicit def async: Async

  def status: StatusBar

  def history: History

  def styling: DynamicStyling

  def dispose(): Unit

  var file: Option[File]

  def isDirty: Boolean

  def fileType: FileType

  /** The editor's content. When changed,
    * the undo history is erased.
    */
  var text: String

  var split: Boolean

  var structureVisible: Boolean

  /** Adds a custom painter to all editors, inserting it at its layer position. */
  def addPainter(p: Painter): Unit

  /** Removes a custom painter from all editors. */
  def removePainter(p: Painter): Unit

  /** Registers a custom action with all editors. */
  def addAction(a: Action): Unit

  /** Unregisters a custom action from all editors. */
  def removeAction(a: Action): Unit
}
