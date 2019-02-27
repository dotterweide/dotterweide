//package dotterweide.languages.scala
//
//import scala.reflect.internal.util.SourceFile
//import scala.tools.nsc.Settings
//import scala.tools.nsc.interactive.{CancelException, Global}
//import scala.tools.nsc.reporters.Reporter
//
//class ScalaCompiler(settings: Settings, _reporter: Reporter) extends Global(settings, _reporter) {
//  override def waitLoadedTyped(source: SourceFile, response: Response[Tree], keepLoaded: Boolean = false, onSameThread: Boolean = true): Unit = {
//    getUnit(source) match {
//      case Some(unit) =>
//        if (unit.isUpToDate) {
//          debugLog("already typed")
//          response set unit.body
//        } else if (ignoredFiles(source.file)) {
//          response.raise(lastException.getOrElse(CancelException))
//        } else if (onSameThread) {
//          getTypedTree(source, forceReload = false, response)
//        } else {
//          debugLog("wait for later")
//          outOfDate = true
//          waitLoadedTypeResponses(source) += response
//        }
//      case None =>
//        debugLog("load unit and type")
//        try reloadSources(List(source))
//        finally {
//          waitLoadedTyped(source, response, onSameThread)
//          if (!keepLoaded) removeUnitOf(source)
//        }
//    }
//  }
//
//  override def typedTree(source: SourceFile, forceReload: Boolean): Tree = {
//    informIDE("typedTree " + source + " forceReload: " + forceReload)
//    val unit = getOrCreateUnitOf(source)
//    if (forceReload) reset(unit)
//    parseAndEnter(unit)
//    if (unit.status <= PartiallyChecked) typeCheck(unit)
//    unit.body
//  }
//}
