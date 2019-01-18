/*
 *  FunctionDeclarationTranslator.scala
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
import dotterweide.languages.toy.node.FunctionDeclaration

trait FunctionDeclarationTranslator extends ToyTranslatable { self: FunctionDeclaration =>
  private val Template =
    """
      |.method private %s(%s)%s
      |   .limit stack 10
      |   .limit locals 10
      |
      |   %s
      |
      |   return
      |.end method
      |""".stripMargin

  override def translate(name: String, labels: Labels): Code = {
    val b = block.getOrElse(
      interrupt("Function block not found: %s", span.text))

    val returnType = nodeType.getOrElse(
      interrupt("Unknown function return type: %s", span.text))

    val parameterTypes = parameters.map { it =>
      it.nodeType.getOrElse(
        interrupt("Unknown parameter type: %s", it.span.text))
    }

    val s = Template.format(identifier,
      parameterTypes.map(_.descriptor).mkString(""),
      returnType.descriptor,
      b.translate(name, new Labels()).instructions)

    Code(methods = s)
  }
}