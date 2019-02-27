package scala.tools.nsc.interactive

import scala.reflect.internal.util.SourceFile
import scala.tools.nsc.util.WorkScheduler

/** An access point to package private methods. */
object DotterweidePeek {
  // N.B.: default arguments are broken with respect to mixing with dependent type arguments
  def waitLoadedTyped(c: Global)(source: SourceFile, response: Response[c.Tree],
                                 keepLoaded: Boolean, onSameThread: Boolean): Unit =
    c.waitLoadedTyped(source, response, keepLoaded = keepLoaded, onSameThread = onSameThread)

  def reloadSource(c: Global)(source: SourceFile): Unit =
    c.reloadSource(source)

  def typedTree(c: Global)(source: SourceFile, forceReload: Boolean): c.Tree = {
    c.typedTree(source, forceReload)
  }

  def scheduler(c: Global): WorkScheduler = c.scheduler
  def scheduler_=(c: Global)(value: WorkScheduler): Unit = c.scheduler = value

//  def minRunId(c: Global): Int = c.minRunId
//  def minRunId_=(c: Global)(value: Int): Unit = c.minRunId = value

}
