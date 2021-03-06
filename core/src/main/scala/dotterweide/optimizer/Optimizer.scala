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

package dotterweide.optimizer

import dotterweide.document.Document
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object Optimizer {
  def optimize(root: Node, document: Document): Unit = {
    optimizationsIn(root).reverse.foreach(p => document.replace(p._1.span.interval, p._2))
  }

  private def optimizationsIn(node: Node): ISeq[(Node, String)] = node.optimized match {
    case Some(s)  => if (node.span.text == s) Nil else (node -> s) :: Nil
    case None     => node.children.flatMap(optimizationsIn)
  }
}