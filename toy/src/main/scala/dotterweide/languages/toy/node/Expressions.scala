/*
 *  Expressions.scala
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
import dotterweide.languages.toy.ToyType._
import dotterweide.languages.toy.compiler.{BinaryExpressionTranslator, CallExpTranslator, GroupTranslator, LiteralTranslator, PrefixExpressionTranslator}
import dotterweide.languages.toy.interpreter.{BinaryExpressionEvaluator, CallExpEvaluator, GroupEvaluator, LiteralEvaluator, PrefixExpressionEvaluator, TypeCheck}
import dotterweide.languages.toy.optimizer.ToyExpressionOptimizer
import dotterweide.lexer.{Token, TokenKind}
import dotterweide.node._
import dotterweide.node.impl.NodeImpl

import scala.collection.immutable.{Seq => ISeq}

trait ToyExpression extends Expression with ToyExpressionOptimizer

class Literal extends NodeImpl("literal")
with ToyExpression with LiteralEvaluator with TypeCheck with LiteralTranslator {
  protected def tokenKind: Option[TokenKind] = children.headOption.flatMap(_.token).map(_.kind)

  override def constant = true

  lazy val nodeType: Option[ToyType with Product with Serializable] = tokenKind.collect {
    case STRING_LITERAL  => StringType
    case NUMBER_LITERAL  => IntegerType
    case BOOLEAN_LITERAL => BooleanType
  }

  override def toString: String = "%s(%s)".format(kind, span.text)
}

class PrefixExpression extends NodeImpl("prefixExpression")
with ToyExpression with PrefixExpressionEvaluator with TypeCheck with PrefixExpressionTranslator {
  def prefix: Option[Token] = children.headOption.flatMap(_.token)

  def expression: Option[Expression] = children.findBy[Expression]

  override def constant: Boolean = expression.exists(_.constant)

  lazy val nodeType: Option[ToyType] = {
    val kindOpt: Option[TokenKind]  = prefix    .map    (_.kind)
    val nodeOpt: Option[NodeType]   = expression.flatMap(_.nodeType)
    (kindOpt, nodeOpt) match {
      case (Some(BANG        ), Some(BooleanType))  => Some(BooleanType)
      case (Some(PLUS | MINUS), Some(IntegerType))  => Some(IntegerType)
      case _                                        => None
    }
  }
}

object PrefixExpression {
  def unapply(node: PrefixExpression) = Some((node.prefix, node.expression))
}

class BinaryExpression extends NodeImpl("binaryExpression")
with ToyExpression with BinaryExpressionEvaluator with TypeCheck with BinaryExpressionTranslator {
  def parts: Option[(Expression, Token, Expression)] = children match {
    case (left: Expression) :: NodeToken(_token) :: (right: Expression) :: Nil => Some((left, _token, right))
    case _ => None
  }

  override def constant: Boolean = children match {
    case (l: Expression) :: _ ::  (r: Expression) :: Nil if l.constant && r.constant => true
    case (l @ Expression(BooleanType)) :: NodeToken(Token(_kind, _, _)) ::  Expression(BooleanType) :: Nil =>
      _kind match {
        case AMP_AMP => l.optimized.contains("false")
        case BAR_BAR => l.optimized.contains("true")
        case _ => false
      }
    case _ => false
  }

  private def signature = parts collect {
    case (Expression(leftType), _token, Expression(rightType)) => (leftType, _token.kind, rightType)
  }

  lazy val nodeType: Option[ToyType with Product with Serializable] = signature.collect {
    case (BooleanType, AMP_AMP, BooleanType) => BooleanType

    case (BooleanType, BAR_BAR, BooleanType) => BooleanType

    case (IntegerType, GT, IntegerType) => BooleanType
    case (IntegerType, GT_EQ, IntegerType) => BooleanType
    case (IntegerType, LT, IntegerType) => BooleanType
    case (IntegerType, LT_EQ, IntegerType) => BooleanType

    case (StringType, EQ_EQ, StringType) => BooleanType
    case (IntegerType, EQ_EQ, IntegerType) => BooleanType
    case (BooleanType, EQ_EQ, BooleanType) => BooleanType

    case (StringType, BANG_EQ, StringType) => BooleanType
    case (IntegerType, BANG_EQ, IntegerType) => BooleanType
    case (BooleanType, BANG_EQ, BooleanType) => BooleanType

    case (IntegerType, STAR, IntegerType) => IntegerType
    case (IntegerType, SLASH, IntegerType) => IntegerType
    case (IntegerType, PERCENT, IntegerType) => IntegerType

    case (IntegerType, PLUS, IntegerType) => IntegerType
    case (IntegerType, MINUS, IntegerType) => IntegerType
    case (StringType, PLUS, _) => StringType
  }
}

object BinaryExpression {
  def unapply(exp: BinaryExpression): Option[(Expression, Token, Expression)] = exp.parts
}

class CallExpression extends NodeImpl("callExpression")
with ToyExpression with CallExpEvaluator with TypeCheck with CallExpTranslator {
  def reference: Option[ReferenceToFunction] = children.findBy[ReferenceToFunction]

  def function: Option[FunctionDeclaration] =
    reference.flatMap(_.target).map(_.asInstanceOf[FunctionDeclaration])

  def arguments: Option[Arguments] = children.findBy[Arguments]

  def expressions: ISeq[Expression] = arguments.map(_.expressions).getOrElse(Nil)

  def bindings: (ISeq[(Expression, Parameter)], ISeq[Expression], ISeq[Parameter]) = {
    val parameters = function.map(_.parameters).getOrElse(Nil)
    val es = expressions.iterator
    val ps = parameters.iterator
    (es.zip(ps).toList, es.toList, ps.toList)
  }

  def rightBrace: Option[Node] = arguments.flatMap(_.children.lastOption)

  lazy val nodeType: Option[ToyType with Product with Serializable] = reference.flatMap { it =>
    if (it.predefined) Some(VoidType) else function.flatMap(_.nodeType)
  }
}

class Group extends NodeImpl("group")
with ToyExpression with GroupEvaluator with TypeCheck with GroupTranslator {
  def child: Option[Expression] = children.findBy[Expression]

  override def constant: Boolean = child.exists(_.constant)

  lazy val nodeType: Option[NodeType] = child.flatMap(_.nodeType)
}