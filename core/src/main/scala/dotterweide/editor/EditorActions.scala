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

trait EditorActions {
  def complete          : Action
  def copy              : Action
  def cut               : Action
  def duplicateLine     : Action
  def clearSelection    : Action
  def format            : Action
  def goToDeclaration   : Action
  def indentSelection   : Action
  def moveLineDown      : Action
  def moveLineUp        : Action
  def optimize          : Action
  def paste             : Action
  def redo              : Action
  def removeLine        : Action
  def rename            : Action
  def selectAll         : Action
  def showUsages        : Action
  def toggleLineComment : Action
  def undo              : Action
  def unindentSelection : Action

  def all: Seq[Action] = List(
    complete,
    copy,
    cut,
    duplicateLine,
    clearSelection,
    format,
    goToDeclaration,
    indentSelection,
    moveLineDown,
    moveLineUp,
    optimize,
    paste,
    redo,
    removeLine,
    rename,
    selectAll,
    showUsages,
    toggleLineComment,
    format,
    undo,
    unindentSelection
  )
}