/*
 *  Coloring.scala
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

package com.pavelfatin.toyide.editor

import java.awt.Color

import com.pavelfatin.toyide.Observable
import com.pavelfatin.toyide.lexer.TokenKind

trait Coloring extends Function[String, Color] with Observable {
  def fontFamily: String

  def fontSize: Int

  def attributesFor(kind: TokenKind): Attributes
}

object Coloring {
  val TextForeground            = "TextForeground"
  val TextBackground            = "TextBackground"
  val CurrentLineBackground     = "CurrentLineBackground"
  val CaretForeground           = "CaretForeground"
  val SelectionForeground       = "SelectionForeground"
  val SelectionBackground       = "SelectionBackground"
  val HighlightBackground       = "HighlightBackground"
  val HoverForeground           = "HoverForeground"
  val PairedBraceBackground     = "PairedBraceBackground"
  val UnbalancedBraceBackground = "UnbalancedBraceBackground"
  val RedForeground             = "RedForeground"
  val UnderlineForeground       = "UnderlineForeground"
  val DimForeground             = "DimForeground"
  val FillBackground            = "FillBackground"
  val BooleanLiteral            = "BooleanLiteral"
  val IntegerLiteral            = "IntegerLiteral"
  val CharLiteral               = "CharLiteral"
  val StringLiteral             = "StringLiteral"
  val Keyword                   = "Keyword"
  val Comment                   = "Comment"
}
