/*
 *  ScalaLanguage.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import dotterweide.build.Version
import dotterweide.editor.{Adviser, ColorScheme, Styling}
import dotterweide.formatter.Format
import dotterweide.inspection.Inspection
import dotterweide.languages.scala.node.ScalaTokens._
import dotterweide.lexer.{Lexer, TokenKind}
import dotterweide.parser.Parser
import dotterweide.{Example, FileType, Language}

import scala.collection.immutable.{Seq => ISeq}

class ScalaLanguage(
                     val scalaVersion : Version = Version(2,12,8),
                     val prelude      : String  = "",
                     val postlude     : String  = "",
                     val examples     : ISeq[Example] = Nil
                   )
  extends Language {

  def name        : String = "Scala"
  def description : String = "The Scala programming language"

  private[this] val _lexer  = new ScalaLexer  (scalaVersion = scalaVersion)
  private[this] val _parser = new ScalaParser (scalaVersion = scalaVersion, prelude = prelude, postlude = postlude)

  def lexer: Lexer = _lexer

  def parser: Parser = _parser

  /** A map from color scheme names to the schemes. */
  def stylings: Map[String, Styling] = Map(
    ColorScheme.LightName -> new ScalaStyling(ColorScheme.LightColors),
    ColorScheme.DarkName  -> new ScalaStyling(ColorScheme.DarkColors)
  )

  /** Pairs of tokens which are symmetric and can be highlighted together,
    * such as matching braces.
    */
  def complements: ISeq[(TokenKind, TokenKind)] =
    List((LBRACE, RBRACE), (LPAREN, RPAREN), (LBRACKET, RBRACKET))

  /** Default style for formatting the language with white space. */
  def format: Format = ScalaFormat

  /** The syntactic prefix for line comments. */
  def lineCommentPrefix: String = "//"

  def inspections: ISeq[Inspection] = Nil

  def adviser: Adviser = _parser

  def fileType: FileType = FileType("Scala file", "scala")

  def dispose(): Unit = {
    _parser.dispose()
  }
}