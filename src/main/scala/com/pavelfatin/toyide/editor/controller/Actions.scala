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
import com.pavelfatin.toyide.formatter.Formatter
import com.pavelfatin.toyide.editor._

private class Actions(document: Document, terminal: Terminal, data: Data, adviser: Adviser,
                      formatter: Formatter, tabSize: Int, comment: String, history: History) extends EditorActions {
  private def historical(action: AnAction) = new HistoricalAction(action, document, terminal, history)
    
  val complete = new Complete(document, terminal, data, adviser, history)

  val copy: HistoricalAction = historical(new Copy(document, terminal))

  val cut: HistoricalAction = historical(new Cut(document, terminal))

  val duplicateLine: HistoricalAction = historical(new DuplicateLine(document, terminal))

  val escape: HistoricalAction = historical(new Escape(terminal))

  val format: HistoricalAction = historical(new Format(document, terminal, data, formatter, tabSize))

  val gotoDeclaration: HistoricalAction = historical(new GotoDeclaration(terminal, data))

  val indentSelection: HistoricalAction = historical(new IndentSelection(document, terminal, tabSize))

  val moveLineDown: HistoricalAction = historical(new MoveLineDown(document, terminal))

  val moveLineUp: HistoricalAction = historical(new MoveLineUp(document, terminal))

  val optimize: HistoricalAction = historical(new Optimize(document, terminal, data))

  val paste: HistoricalAction = historical(new Paste(document, terminal))

  val redo = new Redo(document, terminal, history)

  val removeLine: HistoricalAction = historical(new RemoveLine(document, terminal))

  val rename = new Rename(document, terminal, data, history)

  val selectAll: HistoricalAction = historical(new SelectAll(document, terminal))

  val showUsages: HistoricalAction = historical(new ShowUsages(terminal, data))

  val toggleLineComment: HistoricalAction = historical(new ToggleLineComment(document, terminal, comment))

  val undo = new Undo(document, terminal, history)

  val unindentSelection: HistoricalAction = historical(new UnindentSelection(document, terminal, tabSize))
}