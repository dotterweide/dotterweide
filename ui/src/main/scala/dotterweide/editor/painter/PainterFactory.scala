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

object PainterFactory {
  def createPainters(document: Document, terminal: Terminal, data: Data, canvas: Canvas, grid: Grid, lexer: Lexer,
                     matcher: BraceMatcher, errors: ErrorHolder, coloring: Coloring, processor: ActionProcessor): Seq[Painter] = {

    val context           = PainterContext(document, terminal, data, canvas, grid, coloring)
    val errorPainter      = new ErrorPainter(context, errors)
    val selectionPainter  = new SelectionPainter(context)
    val hoverPainter      = new HoverPainter(context)

    val painters = Seq(
      new ImmediateTextPainter(context, lexer, processor),
      new BackgroundPainter(context),
      new CurrentLinePainter(context),
      errorPainter,
      new MatchPainter(context, matcher, processor),
      new HighlightPainter(context),
      hoverPainter,
      selectionPainter,
      new TextPainter(context, lexer, Seq(errorPainter, hoverPainter, selectionPainter)),
      new CaretPainter(context))

    painters
  }
}
