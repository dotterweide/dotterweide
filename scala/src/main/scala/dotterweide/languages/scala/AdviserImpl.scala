/*
 *  AdviserImpl.scala
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

import dotterweide.editor.Adviser

import scala.reflect.internal.util.{Position => _Position}

private trait AdviserImpl {
  _: CompilerActor =>

  protected def runComplete(text0: String, offset0: Int): Adviser.Result = {
    val offset    = offset0 + prelude.length
    val fullText  = (prelude + text0 + postlude).patch(offset, "_CURSOR_ ", 0)
    val srcFile   = c.newSourceFile(fullText)
    val pos       = _Position.offset(srcFile, offset)
    // c.askReset()
    c.newTyperRun()
    // minRunId_=(c)(c.currentRunId)
    val res = c.completionsAt(pos)

    println(s"name = '${res.name}', positionDelta = ${res.positionDelta}; size = ${res.results.size}")
    res.name.decoded -> Nil
  }
}
