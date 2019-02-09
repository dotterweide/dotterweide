/*
 *  EditorTab.scala
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

import java.io.File

import dotterweide.{FileType, ObservableEvents}

object EditorTab {
  sealed trait Update
  case class FileChanged (newFile: Option[File] ) extends Update
  case class SplitChanged(isSplit: Boolean      ) extends Update
  case class DirtyChanged(isDirty: Boolean      ) extends Update
}
trait EditorTab extends ObservableEvents[EditorTab.Update] {
  def fileType: FileType

  var file: Option[File]

  /** The editor's content. When changed,
    * the undo history is erased.
    */
  var text: String

  def isDirty: Boolean

  var split: Boolean
}