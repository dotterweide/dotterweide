/*
 *  Actions.scala
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

package dotterweide.editor.controller

import dotterweide.document.Document
import dotterweide.editor.{Action, Adviser, Async, Data, EditorActions, FontSettings, History, Terminal}
import dotterweide.formatter.Formatter

private class Actions(document: Document, terminal: Terminal, data: Data, adviser: Adviser,
                      formatter: Formatter, tabSize: Int, lineCommentPrefix: String, font: FontSettings,
                      history: History)(implicit async: Async)
  extends EditorActions {

  private def historical(action: Action) = new HistoricalAction(action, document, terminal, history)

  val complete          : Action = new Complete(document, terminal, data, adviser, history)
  val copy              : Action = historical(new Copy(document, terminal))
  val fontEnlarge       : Action = new FontEnlarge(font)
  val fontShrink        : Action = new FontShrink (font)
  val fontReset         : Action = new FontReset  (font)
  val cut               : Action = historical(new Cut(document, terminal))
  val format            : Action = historical(new Format(document, terminal, data, formatter, tabSize = tabSize))
  val goToDeclaration   : Action = historical(new GoToDeclaration(terminal, data))
  val lineDuplicate     : Action = historical(new DuplicateLine (document, terminal))
  val lineMoveDown      : Action = historical(new MoveLineDown  (document, terminal))
  val lineMoveUp        : Action = historical(new MoveLineUp    (document, terminal))
  val lineRemove        : Action = historical(new RemoveLine    (document, terminal))
  val optimize          : Action = historical(new Optimize(document, terminal, data))
  val paste             : Action = historical(new Paste(document, terminal))
  val redo              : Action = new Redo(history)
  val rename            : Action = new Rename(document, terminal, data, history)
  val selectAll         : Action = historical(new SelectAll(document, terminal))
  val selectNone        : Action = historical(new ClearSelection(terminal))
  val selectionIndent   : Action = historical(new IndentSelection  (document, terminal, tabSize = tabSize))
  val selectionUnindent : Action = historical(new UnindentSelection(document, terminal, tabSize = tabSize))
  val showUsages        : Action = historical(new ShowUsages(terminal, data))
  val toggleLineComment : Action = historical(new ToggleLineComment(document, terminal, prefix = lineCommentPrefix))
  val undo              : Action = new Undo(history)
}