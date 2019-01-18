/*
 *  AbstractColoring.scala
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

package com.pavelfatin.toyide.editor

import java.awt.Color

abstract class AbstractColoring(colors: Map[String, Color]) extends Coloring {
  def apply(id: String): Color = colors.getOrElse(id,
    throw new NoSuchElementException("Unknown color ID: " + id))

  def fontFamily = "Monospaced"

  def fontSize = 14
}
