/*
 *  VariableDeclarationTranslator.scala
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
import dotterweide.languages.toy.node.VariableDeclaration

trait VariableDeclarationTranslator extends ToyTranslatable { self: VariableDeclaration =>
  override def translate(name: String, labels: Labels): Code = {
    val variableType = nodeType.getOrElse(
      interrupt("Unknown variable type: %s", span.text))

    val exp = expression.getOrElse(
      interrupt("Initializer expression not found: %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    if ((self: VariableDeclaration).global) {
      val field = ".field private %s %s\n".format(identifier, variableType.descriptor)
      val initializer = "aload_0\n%sputfield %s/%s %s\n".format(expCode, name, identifier, variableType.descriptor)
      Code(withLine(initializer), field)
    } else {
      Code(withLine("%s%cstore %d\n".format(expCode, variableType.prefix, (self: VariableDeclaration).ordinal + 1)))
    }
  }
}