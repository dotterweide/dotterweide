package de.sciss.scalalang

import scala.tools.nsc.Settings
import scala.tools.nsc.interactive.Global
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.settings.ScalaVersion

object ExplorePC {
  def main(args: Array[String]): Unit = run()

  def run(): Unit = {
    val outputDir = new VirtualDirectory("<virtual-dir>", None)
    val settings = new Settings(err => Console.err.println(err))
    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp.value = true
    settings.source.value = ScalaVersion("2.12.4")

    val reporter = new StoreReporter
    val compiler = new Global(settings, reporter)
    import compiler._

    val src = "class Test { val x = 0 }"
    val srcFile = newSourceFile(src)
//    val run = new compiler.Run
    import compiler._

    val respLoad = new Response[Unit]
    println("askReload")
    compiler.askReload(List(srcFile), respLoad)
    respLoad.get.left.get

    val respParsed = new Response[compiler.Tree]
    println("askParsedEntered")
    compiler.askParsedEntered(srcFile, keepLoaded = true, respParsed)
    val treeParsed = respParsed.get.left.get
    println(s"---- TREE ----\n$treeParsed\n")

    val respTypes = new Response[compiler.Tree]
    println("askLoadedTyped")
    compiler.askLoadedTyped(srcFile, keepLoaded = true, respTypes)
    val treeTyped = respTypes.get.left.get
    println(s"---- TREE ----\n$treeTyped\n")
  }
}
