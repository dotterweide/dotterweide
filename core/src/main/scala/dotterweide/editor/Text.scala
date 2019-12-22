/*
 *  Text.scala
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

import java.awt.font.TextAttribute
import java.text.AttributedString

case class Attributes(color: Color, background: Option[Color], weight: Weight, style: Style, underlined: Boolean) {
  def decorate(result: AttributedString, begin: Int, end: Int): Unit =
    if (begin < end) {  // note: we can encounter empty spans, but `addAttribute` requires non-empty span!
      import TextAttribute._
      result                              .addAttribute(FOREGROUND, color           , begin, end)
      background.foreach(color =>   result.addAttribute(BACKGROUND, color           , begin, end))
      if (underlined)               result.addAttribute(UNDERLINE , UNDERLINE_ON    , begin, end)
      if (weight == Weight.Bold   ) result.addAttribute(WEIGHT    , WEIGHT_BOLD     , begin, end)
      if (style  == Style .Italic ) result.addAttribute(POSTURE   , POSTURE_OBLIQUE , begin, end)
    }
}

abstract sealed class Weight

object Weight {
  case object Normal  extends Weight
  case object Bold    extends Weight
}

abstract sealed class Style

object Style {
  case object Ordinary  extends Style
  case object Italic    extends Style
}
