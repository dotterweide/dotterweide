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
    val settings  = new Settings(err => Console.err.println(err))

    settings.outputDirs.setSingleOutput(outputDir)
    settings.usejavacp.value              = true
    settings.YpresentationAnyThread.value = true // needed to print typed tree
    settings.source.value                 = ScalaVersion("2.12.8")

    val reporter = new StoreReporter
    val compiler = new Global(settings, reporter)
    import compiler._

    val src =
      """private final class Test {
        |  val x: Int = // missing RHS
        |}""".stripMargin

    val srcFile = newSourceFile(src)
//    val run = new compiler.Run
    import compiler._

//    val respLoad = new Response[Unit]
//    println("askReload")
//    compiler.askReload(List(srcFile), respLoad)
//    respLoad.get.left.get
//
//    val respParsed = new Response[compiler.Tree]
//    println("askParsedEntered")
//    compiler.askParsedEntered(srcFile, keepLoaded = true, respParsed)
//    val treeParsed: compiler.Tree = respParsed.get.left.get
//    println(s"---- TREE ----\n$treeParsed\n")
//    println(treeParsed.getClass)
//    println(treeParsed.pos)

    val respTypes = new Response[compiler.Tree]
    println("askLoadedTyped")
    compiler.askLoadedTyped(srcFile, keepLoaded = true, respTypes)
    val treeTyped: compiler.Tree = respTypes.get.left.get
    println(s"---- TREE ----\n$treeTyped\n")

    println()
//    println(treeTyped.getClass)
    val pd @ PackageDef(_, stats) = treeTyped
    println(s"pd.pos            = ${pd.pos}")
    val (cd @ ClassDef(cdMods, cdName, _ /* cdTParams */, cdTemp)) :: Nil = stats
    println(s"cd.pos            = ${cd.pos}")
    println(s"cdMods.positions  = ${cdMods.positions}")
//    println(s"cdName.start      = ${cdName.start}")
    println(s"cdName.length     = ${cdName.length()}")
    println(s"cdName.decoded    = ${cdName.decoded}")
    println(s"cdTemp.pos        = ${cdTemp.pos}")
    val Template(cdParents, _ /* cdSelf */, cdBody) = cdTemp
    println(s"cdParents.size    = ${cdParents.size}")
    cdParents.zipWithIndex.foreach { case (p, pi) =>
      println(s"cdParents($pi).pos  = ${p.pos.getClass}") // OffsetPosition for implicit parent `AnyRef`
    }
//    println(s"cdSelf.pos        = ${cdSelf.pos}") // "no position"
//    val ValDef(cdSelfMods, cdSelfName, cdSelfTpt, cdSelfRhs) = cdSelf
//    println(s"cdSelfTpt.pos     = ${cdSelfTpt.pos}") // "no position"
    println(s"cdBody.size       = ${cdBody.size}")
    val /* constructor */ DefDef(_, _, _, _, _, _) :: (vd @ ValDef(vdMods, vcName, vdTpt, vdRhs)) :: /* synthetic accessor */ DefDef(_, _, _, _, _, _) :: Nil = cdBody

    println(s"vd.pos            = ${vd.pos}")
    println(s"vdTpt.pos         = ${vdTpt.pos}")  // RangePosition because explicit
    println(s"vdRhs.pos         = ${vdRhs.pos}")  // OffsetPosition because undefined

//    cdBody.zipWithIndex.foreach { case (b, bi) =>
//      println(s"cdBody($bi)      = ${b.getClass}")
//      println(s"cdBody($bi).pos  = ${b.pos}")
//    }
  }
}
