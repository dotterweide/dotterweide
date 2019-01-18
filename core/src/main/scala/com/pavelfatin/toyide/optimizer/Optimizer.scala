/*
 *  Optimizer.scala
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

package com.pavelfatin.toyide.optimizer

import com.pavelfatin.toyide.document.Document
import com.pavelfatin.toyide.node.Node

object Optimizer {
  def optimize(root: Node, document: Document): Unit = {
    optimizationsIn(root).reverse.foreach(p => document.replace(p._1.span.interval, p._2))
  }

  private def optimizationsIn(node: Node): Seq[(Node, String)] = node.optimized match {
    case Some(s)  => if (node.span.text == s) Nil else (node -> s) :: Nil
    case None     => node.children.flatMap(optimizationsIn)
  }
}