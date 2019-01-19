/*
 *  AnchoredInterval.scala
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

package dotterweide.document

import dotterweide.Interval

/** A pair of anchors tracking the movement of an interval (span) in a text.
  *
  * @param origin     the initial interval
  */
class AnchoredInterval(document: Document, origin: Interval, beginBias: Bias = Bias.Right, endBias: Bias = Bias.Left) {
  private val beginAnchor = document.createAnchorAt(origin.begin, beginBias )
  private val endAnchor   = document.createAnchorAt(origin.end  , endBias   )

  def interval = Interval(beginAnchor.offset, beginAnchor.offset.max(endAnchor.offset))

  def dispose(): Unit = {
    beginAnchor .dispose()
    endAnchor   .dispose()
  }
}
