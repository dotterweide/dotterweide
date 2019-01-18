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
import com.pavelfatin.toyide.editor.{Adviser, Action, Data, EditorActions, History, Terminal}
import com.pavelfatin.toyide.formatter.Formatter

private class Actions(document: Document, terminal: Terminal, data: Data, adviser: Adviser,
                      formatter: Formatter, tabSize: Int, comment: String, history: History) extends EditorActions {
  private def historical(action: Action) = new HistoricalAction(action, document, terminal, history)
    
  val complete          : Action = new Complete(document, terminal, data, adviser, history)
  val copy              : Action = historical(new Copy(document, terminal))
  val cut               : Action = historical(new Cut(document, terminal))
  val duplicateLine     : Action = historical(new DuplicateLine(document, terminal))
  val escape            : Action = historical(new Escape(terminal))
  val format            : Action = historical(new Format(document, terminal, data, formatter, tabSize))
  val gotoDeclaration   : Action = historical(new GotoDeclaration(terminal, data))
  val indentSelection   : Action = historical(new IndentSelection(document, terminal, tabSize))
  val moveLineDown      : Action = historical(new MoveLineDown(document, terminal))
  val moveLineUp        : Action = historical(new MoveLineUp(document, terminal))
  val optimize          : Action = historical(new Optimize(document, terminal, data))
  val paste             : Action = historical(new Paste(document, terminal))
  val redo              : Action = new Redo(document, terminal, history)
  val removeLine        : Action = historical(new RemoveLine(document, terminal))
  val rename            : Action = new Rename(document, terminal, data, history)
  val selectAll         : Action = historical(new SelectAll(document, terminal))
  val showUsages        : Action = historical(new ShowUsages(terminal, data))
  val toggleLineComment : Action = historical(new ToggleLineComment(document, terminal, comment))
  val undo              : Action = new Undo(document, terminal, history)
  val unindentSelection : Action = historical(new UnindentSelection(document, terminal, tabSize))
}