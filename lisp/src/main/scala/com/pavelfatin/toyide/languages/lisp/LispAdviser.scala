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

package com.pavelfatin.toyide.languages.lisp

import com.pavelfatin.toyide.editor.{Adviser, Variant}
import com.pavelfatin.toyide.languages.lisp.core.CoreFunction
import com.pavelfatin.toyide.languages.lisp.library.Library
import com.pavelfatin.toyide.languages.lisp.node.SymbolNode
import com.pavelfatin.toyide.node.Node

object LispAdviser extends Adviser {
  def variants(root: Node, anchor: Node): Seq[Variant] = {
    anchor.parent match {
      case Some(symbol: SymbolNode) =>
        val localSymbols = symbol.accessibleSymbols.map(_.identifier).filter(!_.endsWith(Adviser.Anchor))
        val symbols = localSymbols ++ CoreFunction.Names ++ Library.instance.symbols
        symbols.map(name => Variant(name, name, 0))
      case _ => Nil
    }
  }
}