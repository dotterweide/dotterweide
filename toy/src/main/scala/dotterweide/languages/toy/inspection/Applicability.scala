/*
 *  Applicability.scala
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

package dotterweide.languages.toy.inspection

import dotterweide.inspection.{Inspection, Mark}
import dotterweide.languages.toy.ToyType.VoidType
import dotterweide.languages.toy.node._
import dotterweide.node._

object Applicability extends Inspection {
  val Mismatch  : (String, String) => String  = "Type mismatch, expected: %s, actual: %s".format(_: String, _: String)
  val Missed    : (String, String) => String  = "Function %s: unspecified parameters: %s".format(_: String, _: String)
  val Excessive : String => String            = "Function %s: excessive argument".format(_: String)
  val Void                                    = "Void argument"

  def inspect(node: Node): Seq[Mark] = node match {
    case call: CallExpression =>
      call.reference.flatMap { ref =>
        if (ref.predefined)
          Some(inspectPredefined(call))
        else
          call.function.map(inspect(call, _))
      } getOrElse {
        Nil
      }
    case _ => Nil
 }

  private def inspectPredefined(call: CallExpression): Seq[Mark] =
    call.expressions.filter(_.nodeType.contains(VoidType)).map(Mark(_, Void))

  private def inspect(call: CallExpression, function: FunctionDeclaration): Seq[Mark] = {
    val (bindings, excessive, missed) = call.bindings

    val mismatches = bindings.collect {
      case (exp @ Expression(actual), TypedNode(expected)) if actual != expected =>
        Mark(exp, Mismatch(expected.presentation, actual.presentation))
    }

    val missedMark = call.rightBrace.flatMap { brace =>
      if (missed.isEmpty) None else
        Some(Mark(brace, Missed(function.name, missed.flatMap(_.id).map(_.span.text).mkString(", "))))
    }

    val excessiveMarks = excessive.map(Mark(_, Excessive(function.name)))

    mismatches ++ excessiveMarks ++ missedMark.toSeq
  }
}