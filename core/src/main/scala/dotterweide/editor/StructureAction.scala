/*
 *  StructureAction.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor

import dotterweide.node.Node

trait StructureAction {
  protected def async : Async
  protected def data  : Data

  def apply(): Unit = {
    val fut = data.computeStructure()
    val tr  = async.await(fut)
    for {
      structure <- tr
      root      <- structure
    } {
      applyWithStructure(root)
    }
  }

  protected def applyWithStructure(root: Node): Unit
}
