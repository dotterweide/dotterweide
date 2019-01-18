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

trait Data extends ObservableEvents[DataEvent] {
  def text: String

  def tokens: Seq[Token]

  def structure: Option[Node]

  def errors: Seq[Error]

  def hasFatalErrors: Boolean

  def pass: Pass

  def hasNextPass: Boolean

  def nextPass(): Unit

  def compute(): Unit
}

case class DataEvent(pass: Pass, errors: Seq[Error])

case class Error(interval: Interval, message: String, decoration: Decoration = Decoration.Underline, fatal: Boolean = true)

sealed abstract class Pass(val next: Option[Pass])

object Pass {
  case object Text        extends Pass(Some(Lexer))
  case object Lexer       extends Pass(Some(Parser))
  case object Parser      extends Pass(Some(Inspections))
  case object Inspections extends Pass(None)
}