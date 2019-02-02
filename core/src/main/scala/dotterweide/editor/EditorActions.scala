/*
 *  EditorActions.scala
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

import scala.collection.immutable.{Seq => ISeq}

trait EditorActions {
  def complete          : Action
  def copy              : Action
  def cut               : Action
  def fontEnlarge       : Action
  def fontShrink        : Action
  def fontReset         : Action
  def format            : Action
  def goToDeclaration   : Action
  def lineDuplicate     : Action
  def lineMoveDown      : Action
  def lineMoveUp        : Action
  def lineRemove        : Action
  def optimize          : Action
  def paste             : Action
  def redo              : Action
  def rename            : Action
  def selectAll         : Action
  def selectNone        : Action
  def selectionIndent   : Action
  def selectionUnindent : Action
  def showUsages        : Action
  def toggleLineComment : Action
  def undo              : Action

  def all: ISeq[Action] = List(
    complete,
    copy,
    cut,
    fontEnlarge,
    fontShrink,
    fontReset,
    format,
    goToDeclaration,
    lineDuplicate,
    lineMoveDown,
    lineMoveUp,
    lineRemove,
    optimize,
    paste,
    redo,
    rename,
    selectAll,
    selectNone,
    selectionIndent,
    selectionUnindent,
    showUsages,
    toggleLineComment,
    undo
  )
}