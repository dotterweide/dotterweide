/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.editor

trait EditorActions {
  def complete          : Action
  def copy              : Action
  def cut               : Action
  def duplicateLine     : Action
  def escape            : Action
  def format            : Action
  def gotoDeclaration   : Action
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
    escape,
    format,
    gotoDeclaration,
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