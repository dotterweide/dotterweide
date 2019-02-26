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

import dotterweide.editor.{Async, Data, Editor, History}
import dotterweide.{FileType, ObservableEvents}

import scala.collection.immutable.{Seq => ISeq}
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

  def editors: ISeq[Editor]

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
}
