/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.languages.toy.compiler.{AssignmentTranslator, CallTranslator, IfTranslator, ReturnTranslator, WhileTranslator}
import com.pavelfatin.toyide.languages.toy.interpreter.{AssignmentEvaluator, CallEvaluator, IfEvaluator, ReturnEvaluator, WhileEvaluator}
import com.pavelfatin.toyide.node._

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
  def unapply(r: Return) = Some((r.expression, r.expectedType))
}

class While extends NodeImpl("while") with BlockHolder with ConditionHolder with WhileEvaluator with WhileTranslator {
  def expression: Option[Expression] = children.findBy[Expression]
}

class If extends NodeImpl("if") with BlockHolder with ConditionHolder with IfEvaluator with IfTranslator {
  def expression: Option[Expression] = children.findBy[Expression]

  def elseBlock = children.filterBy[Block].lift(1)
}

object If {
  def unapply(node: If) = Some((node.expression, node.block, node.elseBlock))
}

class Call extends NodeImpl("call") with CallEvaluator with CallTranslator {
  def expression: Option[CallExpression] = children.findBy[CallExpression]
}

class Comment extends NodeImpl("comment")

class Empty extends NodeImpl("empty")
