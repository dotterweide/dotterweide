/*
 *  Scope.scala
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

package dotterweide.languages.toy.node

import dotterweide.Extensions._
import dotterweide.languages.toy.compiler.ScopeTranslator
import dotterweide.languages.toy.interpreter.ScopeEvaluator
import dotterweide.node._

trait Scope extends Node with ScopeEvaluator with ScopeTranslator {
  def canRedefineOuterDeclarations: Boolean = parent match {
    case Some(_: FunctionDeclaration) => true
    case _ => false
  }

  def functions: Seq[FunctionDeclaration] =
    children.filterBy[FunctionDeclaration]

  def variables: Seq[VariableDeclaration] =
    children.filterBy[VariableDeclaration]

  def parameters: Seq[Parameter] = parent match {
    case Some(function: FunctionDeclaration) => function.parameters
    case _ => Nil
  }

  def values: Seq[UsableNode] = parameters ++ variables

  def declarations: Seq[UsableNode] = functions ++ values

  def exit: Option[Node] = children.find {
    case _: Return => true
    case If(_, Some(block), Some(elseBlock)) if block.exit.isDefined && elseBlock.exit.isDefined => true
    case _ => false
  }
}

object ScopeDeclarations {
  def unapply(scope: Scope) = Some(scope.declarations)
}

object ScopeExit {
  def unapply(scope: Scope): Option[Node] = scope.exit
}
