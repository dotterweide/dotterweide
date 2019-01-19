/*
 *  PainterFactory.scala
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

package dotterweide.editor.painter

import dotterweide.document.Document
import dotterweide.editor.{ActionProcessor, BraceMatcher, Canvas, Coloring, Data, ErrorHolder, Grid, Terminal}
import dotterweide.lexer.Lexer

import scala.collection.immutable.{Seq => ISeq}

object PainterFactory {
  /** Creates a standard stack of painters. The order of the stack:
    *
    * - `ImmediateTextPainter`  : quite a hack to perform fast character insertions and deletions
    * - `BackgroundPainter`     : fill entire background
    * - `CurrentLinePainter`    : fill background of cursor's line
    * - `ErrorPainter`          : paint some error highlights, collect others as decorations
    * - `MatchPainter`          : paired or unbalanced braces (background)
    * - `HighlightPainter`      : `terminal.highlights` background
    * - `HoverPainter`          : collects decorations for `terminal.hover` (blue underlined text)
    * - `SelectionPainter`      : fill selection backgrounds, collect as decorations
    * - `TextPainter`           : text foreground painting, including error, hover, and selection decorations
    * - `CaretPainter`          : cursor position
    */
  def createPainters(document: Document, terminal: Terminal, data: Data, canvas: Canvas, grid: Grid, lexer: Lexer,
                     matcher: BraceMatcher, errors: ErrorHolder, coloring: Coloring,
                     processor: ActionProcessor): ISeq[Painter] = {

    val context           = PainterContext(document, terminal, data, canvas, grid, coloring)
    val errorPainter      = new ErrorPainter    (context, errors)
    val hoverPainter      = new HoverPainter    (context)
    val selectionPainter  = new SelectionPainter(context)

    val painters = List(
      new ImmediateTextPainter(context, lexer, processor),
      new BackgroundPainter   (context),
      new CurrentLinePainter  (context),
      errorPainter,
      new MatchPainter        (context, matcher, processor),
      new HighlightPainter    (context),
      hoverPainter,
      selectionPainter,
      new TextPainter         (context, lexer, List(errorPainter, hoverPainter, selectionPainter)),
      new CaretPainter        (context)
    )

    painters
  }
}
