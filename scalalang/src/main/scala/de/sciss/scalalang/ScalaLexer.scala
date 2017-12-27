package de.sciss.scalalang

import com.pavelfatin.toyide.lexer.{Lexer, Token}

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.{Global, Response}
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion

object ScalaLexer extends Lexer {
  private lazy val global = {
    val outputDir = new VirtualDirectory("<virtual-dir>", None)
    val settings  = new Settings(Console.err.println)
    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp              .value = true
    settings.YpresentationAnyThread .value = true // needed to print typed tree
    settings.source                 .value = ScalaVersion("2.12.4")

    val reporter = new StoreReporter
    new Global(settings, reporter)
  }

  def analyze(input: CharSequence): Iterator[Token] = {
    val src         = input.toString
    val srcFile     = global.newSourceFile(src)
    val respParsed  = new Response[global.Tree]
    global.askParsedEntered(srcFile, keepLoaded = true, respParsed)
    val treeParsed = respParsed.get.left.get // .asInstanceOf[PackageDef]


//    println(s"analyze: ${treeParsed.getClass}")
//
//    Iterator.single(Token(TokenKind("foo"), Span(input, 0, input.length()), problem = Some("not yet parsed")))
    mkIter(treeParsed)
  }

  private def mkIter(t: Global#Tree): Iterator[Token] = t match {
    case global.PackageDef(_, stats) => stats.iterator.flatMap(mkIter)
    case other => println(s"UNSUPPORTED: ${other.getClass}"); ???
  }

  private final class ItPackageDef(df: global.PackageDef) extends Iterator[Token] {
    def hasNext: Boolean = ???

    def next(): Token = ???
  }
}