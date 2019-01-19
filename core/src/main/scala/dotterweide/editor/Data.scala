/*
 *  Data.scala
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

import dotterweide.inspection.Decoration
import dotterweide.lexer.Token
import dotterweide.node.Node
import dotterweide.{Interval, ObservableEvents}

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.Future

// XXX TODO --- the model currently assumes synchronous operation
// of `nextPass` and `compute`. This is at odds with a language server
// approach, and also blocks the EDT if any of these take time, such
// as invoking the Scala presentation compiler.

/** Holds the current state of the editor raw data and analyses. */
trait Data extends ObservableEvents[DataEvent] {
  /** The entire document's text */
  def text: String

  /** The result of the `Lexer`, or empty if in a pass before lexer. */
  def tokens: ISeq[Token]

  /** The result of the `parser`, or empty if in a pass before parser. */
  def structure: Option[Node]

  def computeStructure(): Future[Option[Node]]

  def errors: ISeq[Error]

  def hasFatalErrors: Boolean

  /** The current pass. */
  def pass: Pass

  /** If there is a follow-up pass. */
  def hasNextPass: Boolean

  /** Runs the next pass. Throws an exception if `!hasNextPass` */
  def nextPass(): Unit

//  /** Iterates through all passes (until `!hasNextPass`). */
//  def compute(): Unit
}

case class DataEvent(pass: Pass, errors: ISeq[Error])

case class Error(interval: Interval, message: String, decoration: Decoration = Decoration.Underline,
                 fatal: Boolean = true)

sealed abstract class Pass(val next: Option[Pass], val stage: Int)

object Pass {
  implicit object ordering extends Ordering[Pass] {
    def compare(x: Pass, y: Pass): Int = x.stage compareTo y.stage
  }

  case object Text        extends Pass(next = Some(Lexer    )   , stage = 0)
  case object Lexer       extends Pass(next = Some(Parser   )   , stage = 1)
  case object Parser      extends Pass(next = Some(Inspections) , stage = 2)
  case object Inspections extends Pass(next = None              , stage = 3)
}