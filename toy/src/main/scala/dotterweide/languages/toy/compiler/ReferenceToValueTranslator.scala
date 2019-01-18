/*
 *  ReferenceToValueTranslator.scala
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
import dotterweide.languages.toy.node.{Parameter, ReferenceToValue, VariableDeclaration}

trait ReferenceToValueTranslator extends ToyTranslatable { self: ReferenceToValue =>
  override def translate(name: String, labels: Labels): Code = {
    val node = target.getOrElse(
      interrupt("Target value not found: %s", identifier))

    val referenceType = nodeType.getOrElse(
      interrupt("Unknown target value type: %s", identifier))

    val s = node match {
      case v: VariableDeclaration =>
        if (v.global) {
          "aload_0\ngetfield %s/%s %s\n".format(name, identifier, referenceType.descriptor)
        } else {
          "%cload %d\n".format(referenceType.prefix, v.ordinal + 1)
        }
      case p: Parameter =>
        "%cload %d\n".format(referenceType.prefix, p.ordinal + 1)
      case _ =>
        interrupt("Non-value target for reference %s: %s", identifier, node.span.text)
    }

    Code(withLine(s))
  }
}