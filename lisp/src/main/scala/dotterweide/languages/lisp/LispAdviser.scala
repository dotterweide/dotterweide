/*
 *  LispAdviser.scala
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

package dotterweide.languages.lisp

import dotterweide.editor.{SyncAdviser, Variant}
import dotterweide.languages.lisp.core.CoreFunction
import dotterweide.languages.lisp.library.Library
import dotterweide.languages.lisp.node.SymbolNode
import dotterweide.node.Node

import scala.collection.immutable.{Seq => ISeq}

object LispAdviser extends SyncAdviser {
  def variants(root: Node, anchorNode: Node): ISeq[Variant] = {
    anchorNode.parent match {
      case Some(symbol: SymbolNode) =>
        val localSymbols = symbol.accessibleSymbols.map(_.identifier).filter(!_.endsWith(anchorLabel))
        val symbols = localSymbols ++ CoreFunction.Names ++ Library.instance.symbols
        symbols.map(name => Variant(name, name, 0))
      case _ => Nil
    }
  }
}