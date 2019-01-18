/*
 *  Statements.scala
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
import dotterweide.languages.toy.ToyTokens._
import dotterweide.languages.toy.ToyType
import dotterweide.languages.toy.compiler.{AssignmentTranslator, CallTranslator, IfTranslator, ReturnTranslator, WhileTranslator}
import dotterweide.languages.toy.interpreter.{AssignmentEvaluator, CallEvaluator, IfEvaluator, ReturnEvaluator, WhileEvaluator}
import dotterweide.node._

class Assignment extends NodeImpl("assignment")
with ExpressionHolder with AssignmentEvaluator with AssignmentTranslator {
  def reference: Option[ReferenceNode] = children.findBy[ReferenceNode]

  def expression: Option[Expression] = children.dropWhile(!_.token.exists(_.kind == EQ)).findBy[Expression]

  def expectedType: Option[NodeType] = reference.flatMap(_.target).collect {
    case TypedNode(nodeType) => nodeType
  }
}

class Return extends NodeImpl("return") with ExpressionHolder with ReturnEvaluator with ReturnTranslator {
  def expression: Option[Expression] = children.findBy[Expression]

  def expectedType: Option[ToyType with Product with Serializable] = parents.findBy[FunctionDeclaration].flatMap(_.nodeType)
}

object Return {
  def unapply(r: Return): Option[(Option[Expression], Option[ToyType])] =
    Some((r.expression, r.expectedType))
}

class While extends NodeImpl("while") with BlockHolder with ConditionHolder with WhileEvaluator with WhileTranslator {
  def expression: Option[Expression] = children.findBy[Expression]
}

class If extends NodeImpl("if") with BlockHolder with ConditionHolder with IfEvaluator with IfTranslator {
  def expression: Option[Expression] = children.findBy[Expression]

  def elseBlock: Option[Block] = children.filterBy[Block].lift(1)
}

object If {
  def unapply(node: If): Option[(Option[Expression], Option[Block], Option[Block])] =
    Some((node.expression, node.block, node.elseBlock))
}

class Call extends NodeImpl("call") with CallEvaluator with CallTranslator {
  def expression: Option[CallExpression] = children.findBy[CallExpression]
}

class Comment extends NodeImpl("comment")

class Empty extends NodeImpl("empty")
