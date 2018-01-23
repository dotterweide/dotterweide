/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.toy.node

import com.pavelfatin.toyide.Extensions._
import com.pavelfatin.toyide.languages.toy.ToyTokens._
import com.pavelfatin.toyide.languages.toy.ToyType
import com.pavelfatin.toyide.languages.toy.ToyType._
import com.pavelfatin.toyide.languages.toy.compiler.{FunctionDeclarationTranslator, VariableDeclarationTranslator}
import com.pavelfatin.toyide.languages.toy.interpreter.VariableDeclarationEvaluator
import com.pavelfatin.toyide.lexer.TokenKind
import com.pavelfatin.toyide.node._

class Block extends NodeImpl("block") with Scope

class Program extends NodeImpl("program") with Scope

trait UsableNode extends IdentifiedNode {
  def scope: Option[Scope] = parents.findBy[Scope]

  def usages: Seq[ReferenceNode] = {
    scope match {
      case Some(it) => it.elements.filterBy[ReferenceNode].filter(_.isReferenceTo(this))
      case None => Nil
    }
  }
}

class FunctionDeclaration extends NodeImpl("function") with BlockHolder
with UsableNode with NamedNode with TypedNode with FunctionDeclarationTranslator {
  def id: Option[NodeImpl] = children.drop(1).headOption

  def parametersNode: Option[Parameters] = children.findBy[Parameters]

  def parameters: Seq[Parameter] = parametersNode.map(_.parameters).getOrElse(Nil)

  def name: String = "%s(%s): %s".format(
    identifier, parameters.map(_.name).mkString(", "), nodeType.map(_.presentation).getOrElse("undefined"))

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId: Option[Node] = typeSpec.flatMap(_.typeId)

  def nodeType: Option[ToyType with Product with Serializable] = typeSpec.flatMap(_.declaredType)
}

object FunctionBlock {
  def unapply(function: FunctionDeclaration): Option[Block] = function.block
}

class VariableDeclaration extends NodeImpl("variable") with UsableNode with NamedNode
  with TypedNode with ExpressionHolder with VariableDeclarationEvaluator with VariableDeclarationTranslator {

  def id: Option[NodeImpl] = children.drop(1).headOption

  def name: String = "%s: %s".format(
    identifier, nodeType.map(_.presentation).getOrElse("undefined"))

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId: Option[Node] = typeSpec.flatMap(_.typeId)

  def nodeType: Option[ToyType with Product with Serializable] = typeSpec.flatMap(_.declaredType)

  def expression: Option[Expression] = children.dropWhile(!_.token.exists(_.kind == EQ)).findBy[Expression]

  def expectedType: Option[ToyType with Product with Serializable] = nodeType

  def local: Boolean = parents.findBy[FunctionDeclaration].isDefined
}

class TypeSpec extends NodeImpl("typeSpec") {
  def typeId: Option[Node] = children.lastOption

  def declaredType: Option[ToyType with Product with Serializable] = typeId.flatMap(_.token).flatMap(token => from(token.kind))

  private def from(kind: TokenKind) = Some(kind).collect {
    case STRING => StringType
    case INTEGER => IntegerType
    case BOOLEAN => BooleanType
    case VOID => VoidType
  }
}

class Parameter extends NodeImpl("parameter") with UsableNode with NamedNode with TypedNode {
  def id: Option[NodeImpl] = children.headOption

  override def scope: Option[Scope] = parent match {
    case Some(_: Parameters) => parents.findBy[FunctionDeclaration].flatMap(_.block)
    case _ => super.scope
  }

  def name: String = "%s: %s".format(identifier, typeSpec.flatMap(_.typeId).map(_.span.text).mkString)

  def typeSpec: Option[TypeSpec] = children.findBy[TypeSpec]

  def typeId: Option[Node] = typeSpec.flatMap(_.typeId)

  def nodeType: Option[ToyType with Product with Serializable] = typeSpec.flatMap(_.declaredType)
}

class Parameters extends NodeImpl("parameters") {
  def parameters: Seq[Parameter] = children.filterBy[Parameter]
}

class Arguments extends NodeImpl("arguments") {
  def expressions: Seq[Expression] = children.filterBy[Expression]
}
