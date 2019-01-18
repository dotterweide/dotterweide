/*
 *  GotoDeclaration.scala
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

package com.pavelfatin.toyide.editor.controller

import com.pavelfatin.toyide.node.{IdentifiedNode, Node}
import com.pavelfatin.toyide.editor.{Action, Terminal, Data}

private class GotoDeclaration(terminal: Terminal, data: Data) extends Action {
  def keys: Seq[String] = List("ctrl pressed B")

  def apply(): Unit = {
    data.compute()
    for (reference <- data.referenceAt(terminal.offset);
         target <- reference.target) {
      terminal.offset = offsetOf(target)
    }
  }

  private def offsetOf(target: Node): Int = {
    target match {
      case IdentifiedNode(id, _) => id.span.begin
      case node => node.span.begin
    }
  }
}