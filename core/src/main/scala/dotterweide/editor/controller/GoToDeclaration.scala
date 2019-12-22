/*
 *  GoToDeclaration.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

/*
 * Original code copyright 2018 Pavel Fatin, https://pavelfatin.com
 * Licensed under the Apache License, Version 2.0 (the "License"): http://www.apache.org/licenses/LICENSE-2.0
 */

package dotterweide.editor.controller

import dotterweide.Platform
import dotterweide.editor.ControllerOps._
import dotterweide.editor.{Action, Async, Data, Terminal}
import dotterweide.node.{IdentifiedNode, Node}

import scala.collection.immutable.{Seq => ISeq}

private class GoToDeclaration(terminal: Terminal, data: Data)(implicit async: Async, p: Platform)
  extends Action {

  def name: String        = "Go to Declaration"
  def mnemonic: Char      = 'G'
  val keys: ISeq[String]  = s"${p.menuModifier} pressed B" :: Nil

  def apply(): Unit = {
    val fut = data.computeStructure()
    val tr  = async.await(fut)
    for {
      _         <- tr
      reference <- data.referenceAt(terminal.offset)
      target    <- reference.target
    } {
      // assert (EventQueue.isDispatchThread)
      terminal.offset = offsetOf(target)
    }
  }

  private def offsetOf(target: Node): Int = {
    val n = target match {
      case IdentifiedNode(id, _)  => id
      case _                      => target
    }
    n.span.start
  }
}