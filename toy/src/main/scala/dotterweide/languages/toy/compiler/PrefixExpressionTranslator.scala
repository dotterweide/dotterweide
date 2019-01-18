/*
 *  PrefixExpressionTranslator.scala
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

import dotterweide.languages.toy.ToyTokens._
import dotterweide.compiler.{Code, Labels}
import dotterweide.languages.toy.node.PrefixExpression

trait PrefixExpressionTranslator extends ToyTranslatable { self: PrefixExpression =>
  override def translate(name: String, labels: Labels): Code = {
    val t = prefix.getOrElse(
      interrupt("Prefix token not found: %s", span.text))

    val exp = expression.getOrElse(
      interrupt("Inner expression not found: %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    val l1 = labels.next()
    val l2 = labels.next()

    val s = t.kind match {
      case PLUS => expCode
      case MINUS => "%sineg\n".format(expCode)
      case BANG => "%sifne %s\niconst_1\ngoto %s\n%s:\niconst_0\n%s:\n".format(expCode, l1, l2, l1, l2)
      case _ => interrupt("Incorrect prefix: %s", t.span.text)
    }

    Code(withLine(s))
  }
}