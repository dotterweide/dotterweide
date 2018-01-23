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

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor.{Adviser, AnAction, Data, EditorActions, History, Terminal}
import com.pavelfatin.toyide.formatter.Formatter

private class Actions(document: Document, terminal: Terminal, data: Data, adviser: Adviser,
                      formatter: Formatter, tabSize: Int, comment: String, history: History) extends EditorActions {
  private def historical(action: AnAction) = new HistoricalAction(action, document, terminal, history)
    
  val complete          : AnAction = new Complete(document, terminal, data, adviser, history)
  val copy              : AnAction = historical(new Copy(document, terminal))
  val cut               : AnAction = historical(new Cut(document, terminal))
  val duplicateLine     : AnAction = historical(new DuplicateLine(document, terminal))
  val escape            : AnAction = historical(new Escape(terminal))
  val format            : AnAction = historical(new Format(document, terminal, data, formatter, tabSize))
  val gotoDeclaration   : AnAction = historical(new GotoDeclaration(terminal, data))
  val indentSelection   : AnAction = historical(new IndentSelection(document, terminal, tabSize))
  val moveLineDown      : AnAction = historical(new MoveLineDown(document, terminal))
  val moveLineUp        : AnAction = historical(new MoveLineUp(document, terminal))
  val optimize          : AnAction = historical(new Optimize(document, terminal, data))
  val paste             : AnAction = historical(new Paste(document, terminal))
  val redo              : AnAction = new Redo(document, terminal, history)
  val removeLine        : AnAction = historical(new RemoveLine(document, terminal))
  val rename            : AnAction = new Rename(document, terminal, data, history)
  val selectAll         : AnAction = historical(new SelectAll(document, terminal))
  val showUsages        : AnAction = historical(new ShowUsages(terminal, data))
  val toggleLineComment : AnAction = historical(new ToggleLineComment(document, terminal, comment))
  val undo              : AnAction = new Undo(document, terminal, history)
  val unindentSelection : AnAction = historical(new UnindentSelection(document, terminal, tabSize))
}