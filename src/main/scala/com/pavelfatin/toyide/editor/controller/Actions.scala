/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.editor._
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