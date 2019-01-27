/*
 *  Adviser.scala
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

package dotterweide.editor

import dotterweide.document.Document
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Adviser {
  val DefaultAnchor = "ANCHOR"
}
trait Adviser {
  /** @param offset the current cursor position
    */
  def variantsAsync(document: Document, data: Data, offset: Int)
                   (implicit async: Async): Future[(String, ISeq[Variant])]
}

trait SyncAdviser extends Adviser {
  def anchorLabel: String = Adviser.DefaultAnchor

  def variantsAsync(document: Document, data: Data, offset: Int)
                   (implicit async: Async): Future[(String, ISeq[Variant])] = {
    document.insert(offset, anchorLabel)
    val fut = data.computeStructure()
    val tr  = async.await(fut)
    document.remove(offset, offset + anchorLabel.length)
    tr match {
      case Success(structure) =>
        val tuple = structure.flatMap { root =>
          root.elements.find(it => it.isLeaf && it.span.text.contains(anchorLabel)).map { anchorNode =>
            val query = document.text(anchorNode.span.begin, offset)
            val list  = variants(root, anchorNode)
            (query, list)
          }
        } .getOrElse(("", Nil))
        Future.successful(tuple)

      case Failure(ex) => Future.failed(ex)
    }
  }

  def variants(root: Node, anchorNode: Node): ISeq[Variant]
}

case class Variant(title: String, content: String, shift: Int) {
  override def toString: String = title
}
