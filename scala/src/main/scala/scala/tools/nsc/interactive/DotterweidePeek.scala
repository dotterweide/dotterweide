package scala.tools.nsc.interactive

import scala.reflect.internal.util.SourceFile

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

//  def minRunId(c: Global): Int = c.minRunId
//  def minRunId_=(c: Global)(value: Int): Unit = c.minRunId = value

}
