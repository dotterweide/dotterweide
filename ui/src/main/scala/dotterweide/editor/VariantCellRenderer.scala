/*
 *  VariantCellRenderer.scala
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

import java.awt.{Component, Font}

import dotterweide.lexer.Lexer
import javax.swing.{DefaultListCellRenderer, JList, ListCellRenderer}

private class VariantCellRenderer(lexer: Lexer, coloring: Styling) extends ListCellRenderer[AnyRef] {
  private[this] val delegate: ListCellRenderer[AnyRef] = new DefaultListCellRenderer()

  override def getListCellRendererComponent(list: JList[_ <: AnyRef], value: AnyRef, index: Int,
                                            isSelected: Boolean, cellHasFocus: Boolean): Component = {
    val s = value.toString

    val result  = delegate.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus)

    result.setPreferredSize(null)
    val dim = result.getPreferredSize
    // add extra pixels because of drop-shadow problem in Submin
    dim.width  += 6
    dim.height += 2
    result.setPreferredSize(dim)

    val tokens = lexer.analyze(s)
    if (tokens.hasNext) {
      if (coloring.attributesFor(tokens.next().kind).weight == Weight.Bold) {
        val prototype = result.getFont
        result.setFont(new Font(prototype.getFamily, Font.BOLD, prototype.getSize))
      }
    }

    result
  }
}
