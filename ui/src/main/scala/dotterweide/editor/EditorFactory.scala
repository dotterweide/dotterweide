/*
 *  EditorFactory.scala
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

import dotterweide.Language
import dotterweide.document.{Document, DocumentImpl}

object EditorFactory {
  def createEditorFor(language: Language, history: History, coloring: Coloring): Editor = {
    val document  = new DocumentImpl()
    val data      = new DataImpl(document, language.lexer, language.parser, language.inspections)
    val holder    = new ErrorHolderImpl(document, data)

    createEditorFor(document, data, holder, language, history, coloring)
  }

  def createEditorFor(document: Document, data: Data, holder: ErrorHolder, language: Language,
                      history: History, coloring: Coloring): Editor = {

    val listRenderer  = new VariantCellRenderer(language.lexer, coloring)
    val matcher       = new BraceMatcherImpl(language.complements)

    new EditorImpl(document, data, holder, language.lexer, coloring, matcher, language.format,
      language.adviser, listRenderer, language.comment, history)
  }
}