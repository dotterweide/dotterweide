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

package dotterweide.languages.toy.inspection

import dotterweide.Extensions._
import dotterweide.inspection.{Inspection, Mark}
import dotterweide.languages.toy.node.Scope
import dotterweide.node._

import scala.collection.immutable.{Seq => ISeq}

object DuplicateIdentifier extends Inspection {
  val FunctionRedefinition: String => String = "Function %s is already defined in the scope".format(_: String)

  val VariableRedefinition: String => String = "Variable %s is already defined in the scope".format(_: String)

  val ParameterRedefinition: String => String = "Parameter %s is already defined".format(_: String)

  def inspect(node: Node): ISeq[Mark] = node match {
    case scope: Scope =>
      val functions   = clashableIn(scope, _.functions  )
      val variables   = clashableIn(scope, _.variables  )
      val parameters  = clashableIn(scope, _.parameters )

      val functionClashes: ISeq[Mark] = clashesIn(functions).collect {
        case IdentifiedNode(id, identifier) => Mark(id, FunctionRedefinition(identifier))
      }
      val variableClashes: ISeq[Mark] = clashesIn(variables).collect {
        case IdentifiedNode(id, identifier) => Mark(id, VariableRedefinition(identifier))
      }
      val parameterClashes: ISeq[Mark] = clashesIn(parameters).collect {
        case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
      }

      val parameterAndVariableClashes =
        (clashesIn(parameters ++ variables) diff clashesIn(parameters) diff clashesIn(variables)).collect {
          case IdentifiedNode(id, identifier) => Mark(id, ParameterRedefinition(identifier))
        }

      functionClashes ++ parameterClashes ++ parameterAndVariableClashes ++ variableClashes
    case _ => Nil
  }

  private def clashableIn(scope: Scope, extractor: Scope => ISeq[IdentifiedNode]): ISeq[IdentifiedNode] = {
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

  private def clashesIn(nodes: ISeq[IdentifiedNode]): ISeq[IdentifiedNode] = {
    nodes.groupBy(_.identifier).filter(_._2.size > 1).toList.flatMap(_._2.tail)
  }
}