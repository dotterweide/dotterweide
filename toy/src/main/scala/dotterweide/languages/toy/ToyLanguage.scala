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

package dotterweide.languages.toy

import dotterweide.editor.{Adviser, ColorScheme, Styling}
import dotterweide.formatter.Format
import dotterweide.inspection.Inspection
import dotterweide.languages.toy.ToyTokens._
import dotterweide.languages.toy.inspection._
import dotterweide.languages.toy.parser.ProgramParser
import dotterweide.lexer.{Lexer, TokenKind}
import dotterweide.parser.Parser
import dotterweide.{Example, FileType, Language}

import scala.collection.immutable.{Seq => ISeq}

object ToyLanguage extends Language {
  def name = "Toy"

  def description = "C-like imperative language"

  def lexer: Lexer = ToyLexer

  def parser: Parser = ProgramParser

  def colorings: Map[String, Styling] = Map(
    "Light" -> new ToyStyling(ColorScheme.LightColors),
    "Dark"  -> new ToyStyling(ColorScheme.DarkColors))

  def complements: ISeq[(TokenKind, TokenKind)] = List((LBRACE, RBRACE), (LPAREN, RPAREN))

  def format: Format = ToyFormat

  def lineCommentPrefix = "//"

  def inspections: ISeq[Inspection] = List(ReturnOutsideFunction, DuplicateIdentifier, UnresolvedReference,
    VoidValue, Applicability, TypeMismatch, OperatorApplication, IntegerRange, PrefixApplication,
    MissingReturn, UnreachableStatement, UnusedDeclaration, PredefinedIdentifier, Optimization, DivisionByZero)

  def adviser: Adviser = ToyAdviser

  def fileType = FileType("Toy file", "toy")

  def examples: ISeq[Example] = ToyExamples.Values
}