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

package com.pavelfatin.toyide

import com.pavelfatin.toyide.editor.{Adviser, Coloring}
import com.pavelfatin.toyide.formatter.Format
import com.pavelfatin.toyide.inspection.Inspection
import com.pavelfatin.toyide.lexer.{Lexer, TokenKind}
import com.pavelfatin.toyide.parser.Parser

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