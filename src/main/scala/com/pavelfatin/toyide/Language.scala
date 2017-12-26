/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide

import editor.{Adviser, Coloring}
import formatter.Format
import inspection.Inspection
import lexer.{TokenKind, Lexer}
import parser.Parser

/** Access to a programming language, including description, lexer, parser, etc. */
trait Language {
  def name: String

  def description: String

  def lexer: Lexer

  def parser: Parser

  /** A map from color scheme names to the schemes. */
  def colorings: Map[String, Coloring]

  /** Pairs of tokens which are symmetric and can be highlighted together,
    * such as matching braces.
    */
  def complements: Seq[(TokenKind, TokenKind)]

  /** Default style for formatting the language with white space. */
  def format: Format

  /** The syntactic prefix for line comments. */
  def comment: String

  def inspections: Seq[Inspection]

  def adviser: Adviser

  def fileType: FileType

  def examples: Seq[Example]
}