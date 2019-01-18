/*
 *  CallExpTranslator.scala
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
import dotterweide.languages.toy.node.CallExpression

trait CallExpTranslator extends ToyTranslatable { self: CallExpression =>
  override def translate(name: String, labels: Labels): Code = {
    val ref = reference.getOrElse(
      interrupt("Function not found: %s", span.text))

    val s = if (ref.predefined) {
      translatePredefinedCall(name, labels)
    } else {
      translateCall(name, labels)
    }

    Code(withLine(s))
  }

  private def translatePredefinedCall(name: String, labels: Labels) = {
    val ref = reference.getOrElse(
      interrupt("Function not found: %s", span.text))

    val parts = expressions.map { exp =>
      val expType = exp.nodeType.getOrElse(
        interrupt("Unknow expression type: %s", exp.span.text))

      "aload_0\ngetfield %s/out Ljava/io/PrintStream;\n%sinvokevirtual java/io/PrintStream/print(%s)V\n".format(
        name, exp.translate(name, labels).instructions, expType.descriptor)
    }

    val tail = if (ref.identifier == "println")
      "aload_0\ngetfield %s/out Ljava/io/PrintStream;\nldc \"\\n\"\ninvokevirtual java/io/PrintStream/print(Ljava/lang/String;)V\n"
        .format(name)
    else
      ""

    parts.mkString + tail
  }

  private def translateCall(name: String, labels: Labels) = {
    val f = function.getOrElse(
      interrupt("Function not found: %s", span.text))

    val returnType = f.nodeType.getOrElse(
      interrupt("Unknown function return type: %s", span.text))

    val parameterTypes = f.parameters.map { it =>
      it.nodeType.getOrElse(
        interrupt("Unknown parameter type: %s", it.span.text))
    }

    val data = expressions.map(_.translate(name, labels).instructions).mkString

    "aload_0\n%s\ninvokevirtual %s/%s(%s)%s\n".format(
      data, name, f.identifier, parameterTypes.map(_.descriptor).mkString(""), returnType.descriptor)
  }
}