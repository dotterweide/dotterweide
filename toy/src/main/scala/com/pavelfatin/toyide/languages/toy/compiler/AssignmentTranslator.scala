/*
 *  AssignmentTranslator.scala
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
import com.pavelfatin.toyide.languages.toy.node._

trait AssignmentTranslator extends ToyTranslatable { self: Assignment =>
  override def translate(name: String, labels: Labels): Code = {
    val ref = reference match {
      case Some(it: ReferenceToValue) => it
      case Some(_) => interrupt("Incorrect target for assignment %s", span.text)
      case None => interrupt("Reference for assignment not found %s", span.text)
    }

    val target = ref.target.getOrElse(
      interrupt("Target for reference not found %s", ref.span.text))

    val referenceType = ref.nodeType.getOrElse(
      interrupt("Unknown target value type: %s", ref))

    val exp = expression.getOrElse(
      interrupt("Expression not found %s", span.text))

    val expCode = exp.translate(name, labels).instructions

    val s = target match {
      case v: VariableDeclaration =>
        if (v.global) {
          "aload_0\n%sputfield %s/%s %s\n".format(expCode, name, ref.identifier, referenceType.descriptor)
        } else {
          "%s%cstore %d\n".format(expCode, referenceType.prefix, v.ordinal + 1)
        }
      case p: Parameter =>
        "%s%cstore %d\n".format(expCode, referenceType.prefix, p.ordinal + 1)
      case _ =>
        interrupt("Non-value target for reference %s: %s", ref.identifier, target.span.text)
    }

    Code(withLine(s))
  }
}