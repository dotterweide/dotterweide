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

import dotterweide.document.Document
import dotterweide.editor.{Async, Data, Editor, FontSettings, History}
import dotterweide.ide.impl.PanelImpl
import dotterweide.{FileType, Language, ObservableEvents, Platform}

import scala.collection.immutable.{Seq => ISeq}
import scala.swing.Component

object Panel {
  sealed trait Update
  case class EditorFocused(editor : Editor      ) extends Update
  case class FileChanged  (newFile: Option[File]) extends Update
  case class SplitChanged (isSplit: Boolean     ) extends Update
  case class DirtyChanged (isDirty: Boolean     ) extends Update

  def apply(language          : Language,
            text              : String              = "",
            font              : FontSettings        = FontSettings.Default,
            stylingName       : Option[String]      = None,
            console           : Option[Console]     = None,
            preferredGridSize : Option[(Int, Int)]  = None
           ): Panel =
    new PanelImpl(language = language, text0 = text, font = font, stylingName = stylingName,
      console = console, preferredGridSize = preferredGridSize)
}
/** The main IDE component. */
trait Panel extends ObservableEvents[Panel.Update] {
  def component: Component

  def currentEditor: Editor

  def editors: ISeq[Editor]

  def document: Document

  def data: Data

  implicit def async: Async

  implicit def platform: Platform

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

  /** Sets or replaces a custom component shown on the bottom
    * right of the panel, that is on the right hand side of the
    * status bar.
    */
  def setBottomRightComponent(c: Component): Unit
}
