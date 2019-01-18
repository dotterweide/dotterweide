/*
 *  WhileTranslator.scala
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

package dotterweide.languages.toy.compiler

import dotterweide.compiler.{Code, Labels}
import dotterweide.languages.toy.node.While

trait WhileTranslator extends ToyTranslatable { self: While =>
  override def translate(name: String, labels: Labels): Code = {
    val exp = expression.getOrElse(
      interrupt("Expression not found %s", span.text))

    val body = block.getOrElse(
      interrupt("Block not found %s", span.text))

    val l1 = labels.next()
    val l2 = labels.next()

    val s = "%s:\n%sifeq %s\n%sgoto %s\n%s:\n"
      .format(l1, exp.translate(name, labels).instructions, l2, body.translate(name, labels).instructions, l1, l2)

    Code(withLine(s))
  }
}