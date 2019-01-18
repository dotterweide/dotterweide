/*
 *  ToyLanguage.scala
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

package com.pavelfatin.toyide.languages.toy

import com.pavelfatin.toyide.editor.{Adviser, ColorScheme, Coloring}
import com.pavelfatin.toyide.formatter.Format
import com.pavelfatin.toyide.inspection.Inspection
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.inspection._
import com.pavelfatin.toyide.languages.toy.parser.ProgramParser
import com.pavelfatin.toyide.lexer.{Lexer, TokenKind}
import com.pavelfatin.toyide.parser.Parser
import com.pavelfatin.toyide.{Example, FileType, Language}

object ToyLanguage extends Language {
  def name = "Toy"

  def description = "C-like imperative language"

  def lexer: Lexer = ToyLexer

  def parser: Parser = ProgramParser

  def colorings: Map[String, Coloring] = Map(
    "Light" -> new ToyColoring(ColorScheme.LightColors),
    "Dark"  -> new ToyColoring(ColorScheme.DarkColors))

  def complements: Seq[(TokenKind, TokenKind)] = Seq((LBRACE, RBRACE), (LPAREN, RPAREN))

  def format: Format = ToyFormat

  def comment = "//"

  def inspections: Seq[Inspection] = Seq(ReturnOutsideFunction, DuplicateIdentifier, UnresolvedReference,
    VoidValue, Applicability, TypeMismatch, OperatorApplication, IntegerRange, PrefixApplication,
    MissingReturn, UnreachableStatement, UnusedDeclaration, PredefinedIdentifier, Optimization, DivisionByZero)

  def adviser: Adviser = ToyAdviser

  def fileType = FileType("Toy file", "toy")

  def examples: Seq[Example] = ToyExamples.Values
}