/*
 *  Language.scala
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

package dotterweide

import dotterweide.editor.{Adviser, Styling}
import dotterweide.formatter.Format
import dotterweide.inspection.Inspection
import dotterweide.lexer.{Lexer, TokenKind}
import dotterweide.parser.Parser

import scala.collection.immutable.{Seq => ISeq}

/** Access to a programming language, including description, lexer, parser, etc. */
trait Language {
  def name: String

  /** Human readable string for UI presentation. */
  def description: String

  def fileType: FileType

  /** Syntactic analysis. */
  def lexer: Lexer

  /** Grammatical and semantic analysis. */
  def parser: Parser

  /** A map from color scheme names to the schemes. */
  def stylings: Map[String, Styling]

  /** Pairs of tokens which are symmetric and can be highlighted together,
    * such as matching braces.
    */
  def complements: ISeq[(TokenKind, TokenKind)]

  /** Default style for formatting the language with white space. */
  def format: Format

  /** The syntactic prefix for line comments. */
  def lineCommentPrefix: String

  def inspections: ISeq[Inspection]

  def adviser: Adviser

  /** Disposes resources associated with this language instance.
    * The object should not be used beyond this point.
    */
  def dispose(): Unit

  def examples: ISeq[Example]
}