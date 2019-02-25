/*
 *  TextPainter.scala
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

import java.awt.font.TextAttribute
import java.awt.{Graphics, Rectangle}
import java.text.AttributedString

import dotterweide.Interval
import dotterweide.document.Location
import dotterweide.editor.painter.TextPainter._
import dotterweide.editor.{Area, FontSettings, Pass, Styling}
import dotterweide.lexer.{Lexer, Token}

import scala.collection.immutable.{Seq => ISeq}

/** The main painter, responsible for painting the document's text along with previously
  * collected decorations
  *
  * @param lexer    the lexer is merely used for optimized painting, when the current line
  *                 is being edited. it is run ad-hoc on the current text line.
  */
private class TextPainter(context: PainterContext, lexer: Lexer,
                          decorators: ISeq[Decorator]) extends AbstractPainter(context) {

  def id = "text"

  private[this] var string            = EmptyString
  private[this] var stringValid       = true
  private[this] var singleLineChanged = false

  import context.grid.ascent

  document.onChange { event =>
    stringValid = false

    if (canvas.visible) {
      val replacement = event.asReplacement

      if (!contains(replacement.before, '\n') && !contains(replacement.now, '\n')) {
        singleLineChanged = true

        notifyObservers(lineRectangleAt(replacement.start))
      } else {
        notifyObservers(canvas.visibleRectangle)
      }
    }
  }

  private def resetAll(): Unit = {
    stringValid = false
    notifyObservers(canvas.visibleRectangle)
  }

  styling .onChange(resetAll())
  font    .onChange(resetAll())

  override def paint(g: Graphics, bounds: Rectangle): Unit = {
    val area = grid.toArea(bounds)

    if (singleLineChanged && area.height == 1) {
      paintLine(g, area)
    } else {
      if (!stringValid) {
        updateString()
      }
      paintArea(g, area)
    }

    singleLineChanged = false
  }

  private def updateString(): Unit = {
    // ensure we have run at least the lexer, as we use `data.tokens`
    if (data.pass == Pass.Text) {
      data.nextPass()
    }
    string      = render(document.text, data.tokens, styling, font)
    stringValid = true
  }

  private def paintLine(g: Graphics, area: Area): Unit = {
    val rectangle     = grid.toRectangle(area)
    val lineInterval  = document.intervalOf(area.line)
    val lineText      = document.text(lineInterval)

    if (lineText.length > 0) {
      val tokens    = lexer.analyze(lineText).toList
      val string    = render(lineText, tokens, styling, font)

      val decorated = decorate(string, decorators, lineInterval, - lineInterval.start)
      val iterator  = decorated.getIterator

      g.drawString(iterator, rectangle.x, rectangle.y + ascent)
    }
  }

  private def paintArea(g: Graphics, area: Area): Unit = {
    val decorated: AttributedString = decorate(string, decorators, intervalOf(area), 0)

    Range(area.line, (area.line + area.height).min(document.linesCount)).foreach { line =>
      val interval = {
        val lineInterval = document.intervalOf(line)
        val areaInterval = Interval(lineInterval.start + area.indent, lineInterval.start + area.indent + area.width)
        lineInterval.intersection(areaInterval)
      }

      if (!interval.empty) {
        val iterator  = decorated.getIterator(null /* attributes -- all! */, interval.start, interval.stop)
        val p         = grid.toPoint(Location(line, area.indent))
        g.drawString(iterator, p.x, p.y + ascent)
      }
    }
  }
}

private object TextPainter {
  private val EmptyString = new AttributedString("")

  private def render(text: String, tokens: ISeq[Token], styling: Styling, font: FontSettings): AttributedString = {
    val result = new AttributedString(text)

    if (!text.isEmpty) {
      result.addAttribute(TextAttribute.FAMILY, font.family)
      result.addAttribute(TextAttribute.SIZE  , font.size  )

      tokens.foreach { token =>
        val attributes  = styling.attributesFor(token.kind)
        val span        = token.span
        attributes.decorate(result, span.start, span.stop)
      }
    }

    result
  }

  private def decorate(string: AttributedString, decorators: ISeq[Decorator], visible: Interval,
                       shift: Int): AttributedString = {
    val decorations = decorators.flatMap(_.decorations.map(p =>
      (p._1.intersection(visible), p._2)).filterKeys(!_.empty)).toMap

    if (decorations.isEmpty) string else {
      val result = new AttributedString(string.getIterator)

      decorations.foreach { case (interval, attributes) =>
        attributes.foreach { case (key, value) =>
          result.addAttribute(key, value, interval.start + shift, interval.stop + shift)
        }
      }

      result
    }
  }
}
