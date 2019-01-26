/*
 *  package.scala
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

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.node.{IdentifiedNode, Node, ReferenceNode, ReferenceNodeTarget}

import scala.collection.immutable.{Seq => ISeq}

package object controller {
  private[controller] implicit class DataExt(val data: Data) extends AnyVal {
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
      val tgtOpt: Option[IdentifiedNode] = referenceAt(offset).collect {
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

  private[controller] implicit class NodeExt(val node: Node) extends AnyVal {
    def offsetOf(i: Int): Option[Int] =
      if (node.span.touches(i)) Some(i - node.span.begin) else None
  }

  private[controller] implicit class TerminalExt(val terminal: Terminal) extends AnyVal {
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
          val shift = sel.begin + s.length - terminal.offset
          if (shift < 0) terminal.offset += shift
          document.replace(sel, s)
          if (shift > 0) terminal.offset += shift
        case None =>
          document.insert(terminal.offset, s)
          terminal.offset += s.length
      }
  }
}