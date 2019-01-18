/*
 *  ColorScheme.scala
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

import com.pavelfatin.toyide.editor.Coloring._

object ColorScheme {
  val LightColors: Map[String, Color] = Map(
    TextForeground            -> Color.BLACK,
    TextBackground            -> Color.WHITE,
    CurrentLineBackground     -> new Color(255, 255, 215),
    CaretForeground           -> Color.BLACK,
    SelectionForeground       -> Color.WHITE,
    SelectionBackground       -> new Color( 82, 109, 165),
    HighlightBackground       -> new Color(224, 240, 255),
    HoverForeground           -> Color.BLUE,
    PairedBraceBackground     -> new Color(153, 204, 255),
    UnbalancedBraceBackground -> new Color(255, 220, 220),
    RedForeground             -> Color.RED,
    UnderlineForeground       -> Color.RED,
    DimForeground             -> Color.GRAY,
    FillBackground            -> new Color(246, 235, 188),
    BooleanLiteral            -> new Color(  0,   0, 128),
    IntegerLiteral            -> new Color(  0,   0, 255),
    CharLiteral               -> new Color(  0, 128,   0),
    StringLiteral             -> new Color(  0, 128,   0),
    Keyword                   -> new Color(  0,   0, 128),
    Comment                   -> new Color(128, 128, 128)
  )

  val DarkColors: Map[String, Color] = Map(
    TextForeground            -> new Color(245, 245, 245),
    TextBackground            -> new Color( 20,  31,  46),
    CurrentLineBackground     -> new Color( 27,  43,  64),
    CaretForeground           -> Color.WHITE,
    SelectionForeground       -> Color.WHITE,
    SelectionBackground       -> new Color( 55,  87, 128),
    HighlightBackground       -> new Color(  0, 128, 128),
    HoverForeground           -> new Color( 88, 157, 246),
    PairedBraceBackground     -> new Color( 60,  95, 140),
    UnbalancedBraceBackground -> new Color(140,  60, 102),
    RedForeground             -> Color.RED,
    UnderlineForeground       -> new Color(226,  49,  51),
    DimForeground             -> new Color(181, 181, 181),
    FillBackground            -> new Color( 82,  80,  58),
    BooleanLiteral            -> new Color(  0, 153, 255),
    IntegerLiteral            -> new Color(255, 128, 128),
    CharLiteral               -> new Color(160, 255, 160),
    StringLiteral             -> new Color(160, 255, 160),
    Keyword                   -> new Color(  0, 153, 255),
    Comment                   -> new Color( 80, 240,  80)
  )
}


