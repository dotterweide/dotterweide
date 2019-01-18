/*
 *  DuplicateIdentifier.scala
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

package com.pavelfatin.toyide.languages.toy.inspection

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.inspection.{Inspection, Mark}
import com.pavelfatin.toyide.languages.toy.node.Scope
import com.pavelfatin.toyide.node._

object DuplicateIdentifier extends Inspection {
  val FunctionRedefinition: String => String = "Function %s is already defined in the scope".format(_: String)

  val VariableRedefinition: String => String = "Variable %s is already defined in the scope".format(_: String)

  val ParameterRedefinition: String => String = "Parameter %s is already defined".format(_: String)

  def inspect(node: Node): Seq[Mark] = node match {
    case scope: Scope =>
      val functions = clashableIn(scope, _.functions)
      val variables = clashableIn(scope, _.variables)
      val parameters = clashableIn(scope, _.parameters)

      val functionClashes = clashesIn(functions).collect {
        case IdentifiedNode(id, identifier) => Mark(id, FunctionRedefinition(identifier))
      }
      val variableClashes = clashesIn(variables).collect {
        case IdentifiedNode(id, identifier) => Mark(id, VariableRedefinition(identifier))
      }
      val parameterClashes = clashesIn(parameters).collect {
        case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
      }

      val parameterAndVariableClashes =
        (clashesIn(parameters ++ variables) diff clashesIn(parameters) diff clashesIn(variables)).collect {
          case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
        }

      functionClashes ++ parameterClashes ++ parameterAndVariableClashes ++ variableClashes
    case _ => Nil
  }

  private def clashableIn(scope: Scope, extractor: Scope => Seq[IdentifiedNode]): Seq[IdentifiedNode] = {
    val inner = extractor(scope)
    if (scope.canRedefineOuterDeclarations) {
      inner
    } else {
      val outer = scope.parents.findBy[Scope]
        .map(clashableIn(_, extractor)).getOrElse(Nil)
        .filter(_.span.begin < scope.span.begin)
      outer ++ inner
    }
  }

  private def clashesIn(nodes: Seq[IdentifiedNode]): Seq[IdentifiedNode] = {
    nodes.groupBy(_.identifier).filter(_._2.size > 1).toSeq.flatMap(_._2.tail)
  }
}