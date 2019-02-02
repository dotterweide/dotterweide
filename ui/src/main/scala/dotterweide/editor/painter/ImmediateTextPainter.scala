/*
 *  ImmediateTextPainter.scala
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
import java.awt.{Color, Graphics, Rectangle}
import java.text.AttributedString

import dotterweide.document.Replacement
import dotterweide.editor.{ActionFinished, ActionProcessor, ActionStarted, Adviser, Area, Styling}
import dotterweide.lexer.Lexer

/** A text painter which becomes active, whenever the `processor`
  * is in an `ActionStarted(immediate = true)` window. This happens
  * when the controller processes key events which result in deletions or insertions.
  *
  * It's quite a hack, using funky stuff like `g.copyArea` to move pixels around, and
  * being the only painter with `immediate = true` causing special behaviour in the editor implementation.
  * TODO: This needs major reworking to be well integrated.
  */
private class ImmediateTextPainter(context: PainterContext, lexer: Lexer, processor: ActionProcessor)
  extends AbstractPainter(context) {

  private val Pairs = Set("()", "[]", "{}", "\"\"")

  def id = "immediate text"

  override def immediate = true

  private var lastEvent       = Option.empty[Replacement]
  private var immediateAction = false

  import context.grid.ascent

  processor.onChange {
    case ActionStarted(immediate) =>
      immediateAction = immediate
    case ActionFinished =>
      immediateAction = false
  }

  document.onChange { event =>
    if (immediateAction) {
      val replacement = event.asReplacement

      if (isRelevant(replacement)) {
        lastEvent = Some(replacement)

        val lengthBefore  = replacement.before.length
        val lengthAfter   = replacement.after .length
        val endAfter      = replacement.begin + lengthAfter

        val rectangle = rectangleFrom(replacement.begin,
          math.max(lengthBefore, lengthAfter) + tailLengthFrom(endAfter) + 1)
        notifyObservers(rectangle)
      }
    }
  }

  /** A replacement is only considered relevant (to this painter) if it
    * does not contain newlines or insertion of matching braces, or
    * temporary insertion of completion `Anchor`.
    */
  private def isRelevant(replacement: Replacement): Boolean =
    !contains(replacement.before, '\n') && !contains(replacement.after, '\n') &&
      !(replacement.after.length == 2 && Pairs.contains(replacement.after.toString)) &&
        replacement.after != Adviser.DefaultAnchor

  private def tailLengthFrom(offset: Int): Int = {
    val location = document.toLocation(offset)
    document.endOffsetOf(location.line) - offset
  }

  private def rectangleFrom(offset: Int, length: Int): Rectangle = {
    val location  = document.toLocation(offset)
    val area      = Area(location.line, location.indent, length, 1)
    grid.toRectangle(area)
  }

  override def paint(g: Graphics, bounds: Rectangle): Unit = {
    lastEvent.foreach {
      case Replacement(begin, _, before, after) =>
        paintReplacement(g, begin, before, after)
    }
    lastEvent = None
  }

  private def paintReplacement(g: Graphics, begin: Int, before: CharSequence, after: CharSequence): Unit = {
    val endAfter  = begin        + after .length
    val delta     = after.length - before.length

    if (delta != 0) {
      val tailLength = tailLengthFrom(endAfter)

      if (tailLength > 0) {
        val shift     = grid.cellWidth * delta
        val tailAfter = rectangleFrom(endAfter, tailLength)

        g.copyArea(tailAfter.x - shift, tailAfter.y, tailAfter.width, tailAfter.height, shift, 0)
      }

      if (delta < 0) {
        val exposedBackground = rectangleFrom(endAfter + tailLength, -delta + 1)

        g.setColor(backgroundColorAt(begin))
        fill(g, exposedBackground)
      }
    }

    if (after.length > 0) {
      val location = document.toLocation(begin)

      val prefix = document.text(document.startOffsetOf(location.line), endAfter)

      lexer.analyze(prefix).toSeq.lastOption.foreach { token =>
        val area      = Area(location.line, location.indent, after.length, 1)
        val rectangle = grid.toRectangle(area)

        g.setColor(backgroundColorAt(begin))
        fill(g, rectangle)

        val string = new AttributedString(after.toString)
        string.addAttribute(TextAttribute.FAMILY, font.family)
        string.addAttribute(TextAttribute.SIZE  , font.size  )

        val attributes = styling.attributesFor(token.kind)
        attributes.decorate(string, 0, after.length)

        g.drawString(string.getIterator, rectangle.x, rectangle.y + ascent)
      }
    }

    g.setColor(styling(Styling.CaretForeground))
    fill(g, caretRectangleAt(terminal.offset + math.max(0, delta)))
  }

  private def backgroundColorAt(offset: Int): Color = {
    val currentLine = document.lineNumberOf(offset) == document.lineNumberOf(terminal.offset)
    if (currentLine) styling(Styling.CurrentLineBackground) else styling(Styling.TextBackground)
  }
}