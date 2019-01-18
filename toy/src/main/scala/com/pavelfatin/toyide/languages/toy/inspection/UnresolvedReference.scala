/*
 *  UnresolvedReference.scala
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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.inspection.{Decoration, Inspection, Mark}
import com.pavelfatin.toyide.languages.toy.node._
import com.pavelfatin.toyide.node._

object UnresolvedReference extends Inspection {
  val Message: (String, String) => String = "Cannot resolve %s '%s'".format(_: String, _: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case ref @ ReferenceNode(Some(source), None) if !ref.predefined =>
      node match {
        case _: ReferenceToFunction => Mark(node, Message("function", source.span.text), Decoration.Red) :: Nil
        case _: ReferenceToValue    => Mark(node, Message("value"   , source.span.text), Decoration.Red) :: Nil
      }
    case _ => Nil
  }
}