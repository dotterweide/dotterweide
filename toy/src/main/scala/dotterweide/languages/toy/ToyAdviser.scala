/*
 *  ToyAdviser.scala
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
import dotterweide.editor.{SyncAdviser, Variant}
import dotterweide.languages.toy.node._
import dotterweide.node.{Expression, Node, NodeType}

import scala.collection.immutable.{Seq => ISeq}

object ToyAdviser extends SyncAdviser {
  private val PredefinedFunctions   = List("print", "println").map(asFunction)
  private val DefinitionKeywords    = List("def", "var").map(asKeyword)
  private val ControlKeywords       = List("if", "while").map(asControl)
  private val ElseKeyword           = List("else").map(asKeyword)
  private val ReturnKeyword         = List("return").map(asLiteral)
  private val TypeKeywords          = List("string", "integer", "boolean").map(asLiteral)
  private val FunctionTypeKeywords  = List("void", "string", "integer", "boolean").map(asKeyword)
  private val BooleanLiterals       = List("true", "false").map(asLiteral)

  def variants(root: Node, anchor: Node): ISeq[Variant] = {
    val holders: ISeq[Node] = anchor.parents.filterNot(_.isLeaf)

    lazy val elseKeyword = anchor.previousSibling match {
      case Some(statement: If) if statement.elseBlock.isEmpty => ElseKeyword
      case _ => Nil
    }

    holders.headOption match {
      case Some(spec: TypeSpec) =>
        spec.parent match {
          case Some(_: FunctionDeclaration) => FunctionTypeKeywords
          case _ => TypeKeywords
        }
      case Some(_: Expression) =>
        referencesFor(anchor) ++ BooleanLiterals
      case Some(_: Program) =>
        referencesFor(anchor) ++ PredefinedFunctions ++ DefinitionKeywords ++ elseKeyword ++ ControlKeywords
      case Some(_: Block) =>
        val returnKeyword = if (holders.findBy[FunctionDeclaration].isDefined) ReturnKeyword else Nil
        referencesFor(anchor) ++ PredefinedFunctions ++ elseKeyword ++ returnKeyword ++ ControlKeywords
      case _ => Nil
    }
  }

  def typeAt(root: Node, offset: Int): Option[NodeType] = None  // XXX TODO

  private def referencesFor(node: Node): ISeq[Variant] = {
    val declarations = node.parents
      .filterBy[Scope]
      .flatMap(_.declarations)
      .filter(_.span.start < node.span.start)
    
    val parameters = declarations.filterBy[Parameter          ].map(p => asValue    (p.identifier))
    val functions  = declarations.filterBy[FunctionDeclaration].map(f => asFunction (f.identifier))
    val variables  = declarations.filterBy[VariableDeclaration].filterNot(node.parents.contains).map(v => asValue(v.identifier))

    (parameters.sortBy(_.title) ++ functions.sortBy(_.title) ++ variables.sortBy(_.title)).distinct
  }

  private def asFunction(name: String) = Variant(name, name.formatted("%s()"), -1)
  private def asValue   (name: String) = Variant(name, name, 0)
  private def asLiteral (name: String) = Variant(name, name, 0)
  private def asKeyword (name: String) = Variant(name, name.formatted("%s "), 0)
  private def asControl (name: String) = Variant(name, name.formatted("%s ()"), -1)
}