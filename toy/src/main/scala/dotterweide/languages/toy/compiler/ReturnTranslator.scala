/*
 *  ReturnTranslator.scala
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
import dotterweide.languages.toy.node.Return

trait ReturnTranslator extends ToyTranslatable { self: Return =>
  override def translate(name: String, labels: Labels): Code = {
    val returnCode = expression.map { exp =>
      val t = exp.nodeType.getOrElse(
        interrupt("Unknown return expression type: %s", span.text))

      "%s\n%creturn\n".format(exp.translate(name, labels).instructions, t.prefix)
    }
    Code(withLine(returnCode.getOrElse("return\n")))
  }
}