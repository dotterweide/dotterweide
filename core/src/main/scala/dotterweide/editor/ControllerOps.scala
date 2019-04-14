/*
 *  ControllerOps.scala
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

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.node.{IdentifiedNode, Node, ReferenceNode, ReferenceNodeTarget}

import scala.collection.immutable.{Seq => ISeq}

object ControllerOps {
  implicit class DataOps(val data: Data) extends AnyVal {
    def leafAt(offset: Int): Option[Node] =
      for {
        root  <- data.structure
        i     <- root.offsetOf(offset)
        n     <- root.leafAt(i)
      } yield n

    def referenceAt(offset: Int): Option[ReferenceNode] =
      for {
        root  <- data.structure
        i     <- root.offsetOf(offset)
        n     <- root.referenceAt(i)
      } yield n

    def identifierAt(offset: Int): Option[IdentifiedNode] =
      for {
        root  <- data.structure
        i     <- root.offsetOf(offset)
        n     <- root.identifierAt(i)
      } yield n

    def connectedLeafsFor(offset: Int): ISeq[Node] = {
      val refOpt = referenceAt(offset)
      val tgtOpt: Option[IdentifiedNode] = refOpt.collect {
        case ReferenceNodeTarget(node: IdentifiedNode) => node
      } orElse {
        identifierAt(offset)
      }

      tgtOpt match {
        case Some(targetNode) =>
          val refs: List[ReferenceNode] = data.structure.toList.flatMap { root =>
            root.elements.collect {
              case ref @ ReferenceNodeTarget(target) if targetNode == target => ref
            }
          }
          targetNode.id.toList ::: refs.flatMap(_.source)

        case None => Nil
      }
    }
  }

  implicit class NodeOps(val node: Node) extends AnyVal {
    def offsetOf(i: Int): Option[Int] =
      if (node.span.touches(i)) Some(i - node.span.start) else None
  }

  implicit class TerminalOps(val terminal: Terminal) extends AnyVal {
    /** Produces an interval covering the line on which the cursor is currently positioned. */
    def currentLineIntervalIn(document: Document): Interval = {
      val line = document.lineNumberOf(terminal.offset)
      document.intervalOfNl(line)
    }

    def currentLineNumberIn(document: Document): Int =
      document.lineNumberOf(terminal.offset)

    def insertInto(document: Document, s: String): Unit =
      terminal.selection match {
        case Some(sel) =>
          terminal.selection = None
          val shift = sel.start + s.length - terminal.offset
          if (shift < 0) terminal.offset += shift
          document.replace(sel, s)
          if (shift > 0) terminal.offset += shift
        case None =>
          document.insert(terminal.offset, s)
          terminal.offset += s.length
      }
  }
}
