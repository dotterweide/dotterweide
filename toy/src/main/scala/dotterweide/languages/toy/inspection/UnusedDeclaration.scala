/*
 *  UnusedDeclaration.scala
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

import dotterweide.inspection.{Decoration, Inspection, Mark}
import dotterweide.languages.toy.node._
import dotterweide.node.{IdentifiedNode, Node}

import scala.collection.immutable.{Seq => ISeq}

object UnusedDeclaration extends Inspection {
  val Message: (String, String) => String = (entity, name) =>
    "%s '%s' is never used".format(entity.capitalize, name)

  def inspect(node: Node): ISeq[Mark] = node match {
    case ScopeDeclarations(declarations) =>
      val unused = for (declaration <- declarations;
                        elements = declaration.elements
                        if declaration.usages.forall(elements.contains)) yield declaration
      unused.collect {
        case node @ IdentifiedNode(id, identifier) =>
          val entity = node match {
            case _: FunctionDeclaration => "function"
            case _: VariableDeclaration => "variable"
            case _ => "parameter"
          }
          Mark(id, Message(entity, identifier), Decoration.Dim, warning = true)
      }
    case _ => Nil
  }
}