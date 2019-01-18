/*
 *  DynamicColoring.scala
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

package dotterweide.ide

import dotterweide.Observable
import dotterweide.editor.{Attributes, Color, Coloring}
import dotterweide.lexer.TokenKind

private class DynamicColoring(delegates: Map[String, Coloring]) extends Coloring with Observable {
  require (delegates.nonEmpty)

  private var _name     : String    = delegates.head._1
  private var _coloring : Coloring  = delegates.head._2

  def names: Seq[String] = delegates.keys.toSeq
  
  def name: String = _name 
  
  def name_=(name: String): Unit =
    if (_name != name) {
      _name = name
      _coloring = delegates(name)

      notifyObservers()
    }

  def apply(id: String): Color = _coloring(id)

  def fontFamily: String = _coloring.fontFamily

  def fontSize: Int = _coloring.fontSize

  def attributesFor(kind: TokenKind): Attributes = _coloring.attributesFor(kind)
}
