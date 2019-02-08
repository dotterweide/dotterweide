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

import dotterweide.editor.{Action, Async, Data, Terminal}
import dotterweide.node.{IdentifiedNode, Node}

import scala.collection.immutable.{Seq => ISeq}

private class GoToDeclaration(terminal: Terminal, data: Data)(implicit async: Async) extends Action {
  def name: String        = "Go to Declaration"
  def mnemonic: Char      = 'G'
  def keys: ISeq[String]  = "ctrl pressed B" :: Nil

  def apply(): Unit = {
    import async.executionContext
    for {
      _         <- data.computeStructure()
      reference <- data.referenceAt(terminal.offset)
      target    <- reference.target
    } {
      terminal.offset = offsetOf(target)
    }
  }

  private def offsetOf(target: Node): Int = {
    target match {
      case IdentifiedNode(id, _) => id.span.start
      case node => node.span.start
    }
  }
}