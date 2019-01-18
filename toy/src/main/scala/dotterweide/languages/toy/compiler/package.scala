/*
 *  package.scala
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

package dotterweide.languages.toy

import dotterweide.Extensions._
import dotterweide.compiler.TranslationException
import dotterweide.languages.toy.node.{Parameter, Program, Scope, VariableDeclaration}
import dotterweide.node.NodeType

package object compiler {
  implicit class RichToyType(nodeType: NodeType) {
    def descriptor: String = nodeType match {
      case ToyType.StringType => "Ljava/lang/String;"
      case ToyType.IntegerType => "I"
      case ToyType.BooleanType => "Z"
      case ToyType.VoidType => "V"
    }

    def prefix: Char = nodeType match {
      case ToyType.StringType => 'a'
      case ToyType.IntegerType |  ToyType.BooleanType => 'i'
      case ToyType.VoidType => throw TranslationException("No prefix for void type")
    }
  }

  implicit class RichVariableDeclaration(variable: VariableDeclaration) {
    def ordinal: Int = variable.parents
      .filterBy[Scope]
      .init
      .flatMap(_.values)
      .count(_.span.begin < variable.span.begin)

    def global: Boolean = variable.parent match {
      case Some(_: Program) => true
      case _ => false
    }
  }

  implicit class RichParameter(parameter: Parameter) {
    def ordinal: Int = parameter.previousSiblings.filterBy[Parameter].size
  }
}
