/*
 *  AbstractCompilerActor.scala
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

import scala.tools.nsc.interactive.Global

trait AbstractCompilerActor {
  protected val c: Global

  object NamedArg {
    def unapply(tree: c.AssignOrNamedArg): Option[(c.Tree, c.Tree)] =
      c.AssignOrNamedArg.unapply(tree)
  }
}
