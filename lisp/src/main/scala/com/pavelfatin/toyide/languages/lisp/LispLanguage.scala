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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.editor.{Adviser, ColorScheme}
import com.pavelfatin.toyide.formatter.Format
import com.pavelfatin.toyide.lexer.Lexer
import com.pavelfatin.toyide.parser.Parser
import com.pavelfatin.toyide.{Example, FileType, Language}

object LispLanguage extends Language {
  def name = "Lisp"

  def description = "Clojure-like functional language"

  def lexer: Lexer = LispLexer

  def parser: Parser = LispParser

  def colorings = Map(
    "Light" -> new LispColoring(ColorScheme.LightColors),
    "Dark" -> new LispColoring(ColorScheme.DarkColors))

  def complements = Seq(LispTokens.Parens, LispTokens.Brackets)

  def format: Format = LispFormat

  def comment = ";"

  def inspections = Seq()

  def adviser: Adviser = LispAdviser

  def fileType = FileType("Lisp file", "lisp")

  def examples: Seq[Example] = LispExamples.Values
}