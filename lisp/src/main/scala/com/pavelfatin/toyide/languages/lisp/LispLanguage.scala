/*
 *  LispLanguage.scala
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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.editor.{Adviser, ColorScheme, Coloring}
import com.pavelfatin.toyide.formatter.Format
import com.pavelfatin.toyide.inspection.Inspection
import com.pavelfatin.toyide.lexer.{Lexer, TokenKind}
import com.pavelfatin.toyide.parser.Parser
import com.pavelfatin.toyide.{Example, FileType, Language}

object LispLanguage extends Language {
  def name = "Lisp"

  def description = "Clojure-like functional language"

  def lexer: Lexer = LispLexer

  def parser: Parser = LispParser

  def colorings: Map[String, Coloring] = Map(
    "Light" -> new LispColoring(ColorScheme.LightColors),
    "Dark"  -> new LispColoring(ColorScheme.DarkColors))

  def complements: Seq[(TokenKind, TokenKind)] = Seq(LispTokens.Parens, LispTokens.Brackets)

  def format: Format = LispFormat

  def comment = ";"

  def inspections: Seq[Inspection] = Seq()

  def adviser: Adviser = LispAdviser

  def fileType = FileType("Lisp file", "lisp")

  def examples: Seq[Example] = LispExamples.Values
}