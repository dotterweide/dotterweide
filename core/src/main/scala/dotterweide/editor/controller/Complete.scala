/*
 *  Complete.scala
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

import dotterweide.document.Document
import dotterweide.editor.{Action, Adviser, Async, Data, History, Terminal, Variant}
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

private class Complete(document: Document, terminal: Terminal, data: Data,
                       adviser: Adviser, history: History)(implicit async: Async)
  extends Action {

  def name: String        = "Complete"
  def mnemonic: Char      = 'P'
  def keys: ISeq[String]  = "ctrl pressed SPACE" :: Nil

  def apply(): Unit = {
    terminal.selection = None
    terminal.highlights = Nil
    val label = Adviser.Anchor
    document.insert(terminal.offset, label)
    val fut = data.computeStructure()
    val tr  = async.await(fut)
    document.remove(terminal.offset, terminal.offset + label.length)
    for {
      structure <- tr
      root      <- structure
    } {
      applyWithStructure(root, label)
    }
  }

  private def applyWithStructure(root: Node, label: String): Unit =
    for {
//      root    <- structure
      anchor  <- root.elements.find(it => it.isLeaf && it.span.text.contains(label))
    } {
      val variants  = adviser.variants(root, anchor)
      val query     = document.text(anchor.span.begin, terminal.offset)
      val filtered  = variants.filter(_.content.startsWith(query))
      filtered match {
        case Seq() =>
        case Seq(single) => history.capture(document, terminal) {
          insert(single, query)
        }
        case multiple => terminal.choose(multiple, query) { it =>
          history.capture(document, terminal) {
            insert(it, query)
          }
        }
      }
    }

  private def insert(variant: Variant, query: String): Unit = {
    terminal.insertInto(document, variant.content.stripPrefix(query))
    terminal.offset += variant.shift
  }
}