/*
 *  Distance.scala
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

package com.pavelfatin.toyide.formatter

abstract class Distance

object Distance {
  case object Joint           extends Distance
  case object Space           extends Distance
  case class  Spaces(n: Int)  extends Distance
  case object Lines           extends Distance
  case class  Lines(n: Int)   extends Distance
  case object LinesOrSpace    extends Distance
}