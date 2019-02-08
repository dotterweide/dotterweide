/*
 *  MatchPainter.scala
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

import java.awt.{Graphics, Rectangle}

import dotterweide.{Interval, Span}
import dotterweide.document.AnchoredInterval
import dotterweide.editor.{ActionProcessor, Area, BraceMatcher, BraceType, CaretMovement, Color, Styling, DataEvent, FocusChanged, Inapplicable, Paired, Pass, SelectionChange, Unbalanced, VisibleRectangleChanged}
import dotterweide.lexer.Token

import scala.collection.immutable.{Seq => ISeq}

// XXX TODO --- understand what the `Delay` thing is needed

/** A painter for matching (or unbalanced) braces. */
private class MatchPainter(context: PainterContext, matcher: BraceMatcher,
                           processor: ActionProcessor) extends AbstractPainter(context) {

  private var anchoredMatches: ISeq[AnchoredMatch] = Nil

  private var completeData = true

  new Delay(terminal, processor).onChange {
    case CaretMovement(_, _, _)             if data.pass != Pass.Text => update()
    case SelectionChange(_, Some(_), None)  if data.pass != Pass.Text => update()
    case _ =>
  }

  data.onChange {
    case DataEvent(Pass.Lexer, _) => update()
    case _ =>
  }
  
  canvas.onChange {
    case VisibleRectangleChanged(_) if !completeData && anchoredMatches.nonEmpty  => update(complete = true)
    case FocusChanged(_)                                                          => update(complete = true)
    case _ =>
  }

  private def update(complete: Boolean = false): Unit = {
    anchoredMatches.foreach(_.dispose())

    val previousMatches = anchoredMatches

    anchoredMatches = if (!canvas.hasFocus || terminal.selection.isDefined) Nil else {
      val tokens = if (complete) data.tokens else {
        val visibleInterval = intervalOf(grid.toArea(canvas.visibleRectangle))
        data.tokens.filter(_.span.intersectsWith(visibleInterval))
      }
      matchIntervalsIn(tokens, terminal.offset).map(p => new AnchoredMatch(p._1.interval, p._2)).toVector
    }

    previousMatches.foreach(it => notifyObservers(it.interval))
    anchoredMatches.foreach(it => notifyObservers(it.interval))

    completeData = complete
  }

  private def matchIntervalsIn(tokens: ISeq[Token], offset: Int): ISeq[(Span, BraceType)] =
    tokens.flatMap { token =>
      matcher.braceTypeOf(token, data.tokens, offset) match {
        case Paired       => (token.span, Paired)     :: Nil
        case Unbalanced   => (token.span, Unbalanced) :: Nil
        case Inapplicable => Nil
      }
    }

  def id = "match"

  def paint(g: Graphics, bounds: Rectangle): Unit = {
    anchoredMatches.foreach { it =>
      val rectangle = toRectangle(it.interval).intersection(bounds)

      if (!rectangle.isEmpty) {
        val color = colorFor(it.braceType)
        g.setColor(color)
        fill(g, rectangle)
      }
    }
  }

  private def colorFor(braceType: BraceType): Color = braceType match {
    case Paired       => styling(Styling.PairedBraceBackground    )
    case Unbalanced   => styling(Styling.UnbalancedBraceBackground)
    case Inapplicable => styling(Styling.TextBackground           )
  }

  private def toRectangle(interval: Interval): Rectangle = {
    val location = document.toLocation(interval.start)
    val area = Area(location.line, location.indent, interval.length, 1)
    grid.toRectangle(area)
  }

  private class AnchoredMatch(origin: Interval, val braceType: BraceType) extends AnchoredInterval(document, origin)
}
