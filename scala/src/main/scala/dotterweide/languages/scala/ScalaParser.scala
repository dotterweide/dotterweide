/*
 *  ScalaParser.scala
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
import dotterweide.editor.Async
import dotterweide.languages.scala.node.{Block, PackageNode}
import dotterweide.lexer.Token
import dotterweide.node.Node
import dotterweide.parser.Parser

import scala.concurrent.{Future, blocking}
import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion

class ScalaParser extends Parser {
  private val reporter = new StoreReporter

  private lazy val compiler: Global = {
    val outputDir = new VirtualDirectory("<virtual-dir>", None)
    val settings  = new Settings(err => Console.err.println(err))

    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp.value              = true
    settings.YpresentationAnyThread.value = true // needed to print typed tree
    settings.source.value                 = ScalaVersion("2.12.8")

    new Global(settings, reporter)
  }

  private def compile(text: String): compiler.Tree = {
    import compiler._
    val srcFile   = newSourceFile(text)
    val respTypes = new Response[compiler.Tree]
    compiler.askReset()
    compiler.askLoadedTyped(srcFile, keepLoaded = false /* true */, respTypes)
    val treeTyped: compiler.Tree = respTypes.get.left.get
    treeTyped
  }

  def parseAsync(text: String, tokens: Iterator[Token])(implicit async: Async): Future[Node] = {
    import async.executionContext
    Future {
      /* val tree = */ blocking {
        compile(text)
      }
      val n = new PackageNode()
      n.children = reporter.infos.iterator.filter(info => info.pos.isDefined && info.severity.id >= 2).map { info =>
        val child     = new Block()
        child.span    = Span("", info.pos.start, info.pos.end)
        child.problem = Some(info.msg)
        child
      } .toVector
      n
    }
  }
}
