/*
 *  StringValue.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Extensions._

object StringValue {
  def apply(s: String): ListValue =
    ListValue(s.map(CharacterValue))

  def unapply(list: ListValue): Option[String] = {
    val elements = list.content

    if (elements.isEmpty) None else elements.collectAll {
      case CharacterValue(c) => c
    } map { chars =>
      chars.mkString
    }
  }
}
