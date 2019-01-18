/*
 *  LiteralTranslator.scala
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

package com.pavelfatin.toyide.languages.toy.compiler

import com.pavelfatin.toyide.compiler.{Code, Labels}
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.languages.toy.node.Literal

trait LiteralTranslator extends ToyTranslatable { self: Literal =>
  override def translate(name: String, labels: Labels): Code = {
    val s = span.text

    val content = nodeType match {
      case Some(ToyType.StringType) | Some(ToyType.IntegerType) => s
      case Some(ToyType.BooleanType) =>
        s match {
          case "true" => "1"
          case "false" => "0"
          case _ => interrupt("Incorrect literal: %s", s)
        }
      case _ => interrupt("Incorrect literal: %s", s)
    }

    Code(withLine("ldc %s\n".format(content)))
  }
}