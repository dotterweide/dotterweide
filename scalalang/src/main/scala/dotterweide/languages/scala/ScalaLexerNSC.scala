/*
 *  ScalaLexerNSC.scala
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

import dotterweide.Span
import dotterweide.languages.scala.node.ScalaTokens
import dotterweide.lexer.{Lexer, Token}

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion

// this doesn't work because the AST doesn't preserve formatting,
// so we can't flatten the tree to tokens
object ScalaLexerNSC extends Lexer {
  private lazy val global = {
    val outputDir = new VirtualDirectory("<virtual-dir>", None)
    val settings  = new Settings(Console.err.println)
    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp              .value = true
    settings.YpresentationAnyThread .value = true // needed to print typed tree
    settings.source                 .value = ScalaVersion("2.12.8")

    val reporter = new StoreReporter
    new Global(settings, reporter)
  }

  def analyze(input: CharSequence): Iterator[Token] = {
    val src         = input.toString
    val srcFile     = global.newSourceFile(src)
    val respParsed  = new Response[global.Tree]
    global.askParsedEntered(srcFile, keepLoaded = true, respParsed)
    val treeParsed  = respParsed.get.left.get // .asInstanceOf[PackageDef]


//    println(s"analyze: ${treeParsed.getClass}")
//
//    Iterator.single(Token(TokenKind("foo"), Span(input, 0, input.length()), problem = Some("not yet parsed")))
    mkIterator(input, treeParsed)
  }

  private def mkIterator(input: CharSequence, t: Global#Tree): Iterator[Token] = t match {
    case global.PackageDef(_, stats) =>
      val posH = stats.headOption.fold(t.pos)(stat0 => t.pos.withEnd(stat0.pos.start))
      val span = Span(input, posH.start, posH.end)
      Iterator.single(Token(ScalaTokens.PACKAGE, span)) ++ stats.iterator.flatMap(mkIterator(input, _))
    case other => println(s"UNSUPPORTED: ${other.getClass}"); ???
  }

//  private final class ItPackageDef(df: global.PackageDef) extends Iterator[Token] {
//    def hasNext: Boolean = ...
//
//    def next(): Token = ...
//  }
}