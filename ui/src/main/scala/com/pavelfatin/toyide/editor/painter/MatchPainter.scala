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

package com.pavelfatin.toyide.editor.painter

import java.awt.{Graphics, Rectangle}

import com.pavelfatin.toyide.Interval
import com.pavelfatin.toyide.document.AnchoredInterval
import com.pavelfatin.toyide.editor.{ActionProcessor, Area, BraceMatcher, BraceType, CaretMovement, Coloring, DataEvent, FocusChanged, Inapplicable, Paired, Pass, SelectionChange, Unbalanced, VisibleRectangleChanged}
import com.pavelfatin.toyide.lexer.Token

private class MatchPainter(context: PainterContext, matcher: BraceMatcher,
                           processor: ActionProcessor) extends AbstractPainter(context) {

  private var anchoredMatches: Seq[AnchoredMatch] = Nil

  private var completeData = true

  new Delay(terminal, processor).onChange {
    case CaretMovement(_, _)            if data.pass != Pass.Text => update()
    case SelectionChange(Some(_), None) if data.pass != Pass.Text => update()
    case _ =>
  }

  data.onChange {
    case DataEvent(Pass.Lexer, _) => update()
    case _ =>
  }
  
  canvas.onChange {
    case VisibleRectangleChanged(_) if !completeData && anchoredMatches.nonEmpty => update(complete = true)
    case FocusChanged(_) => update(complete = true)
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

  private def matchIntervalsIn(tokens: Seq[Token], offset: Int) = tokens.flatMap { token =>
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

  private def colorFor(braceType: BraceType) = braceType match {
    case Paired       => coloring(Coloring.PairedBraceBackground    )
    case Unbalanced   => coloring(Coloring.UnbalancedBraceBackground)
    case Inapplicable => coloring(Coloring.TextBackground           )
  }

  private def toRectangle(interval: Interval): Rectangle = {
    val location = document.toLocation(interval.begin)
    val area = Area(location.line, location.indent, interval.length, 1)
    grid.toRectangle(area)
  }

  private class AnchoredMatch(origin: Interval, val braceType: BraceType) extends AnchoredInterval(document, origin)
}
