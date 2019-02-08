/*
 *  References.scala
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
import dotterweide.languages.toy.compiler.ReferenceToValueTranslator
import dotterweide.languages.toy.interpreter.{ReferenceToValueEvaluator, TypeCheck}
import dotterweide.node._

trait ToyReference extends ReferenceNode {
  def source: Option[Node] = children.headOption

  protected def targetIn(filter: Scope => Seq[IdentifiedNode]): Option[IdentifiedNode] = source.flatMap { node =>
    parents.filterBy[Scope].flatMap(filter)
      .filter(_.span.start < node.span.start)
      .find(it => it.identifier == identifier)
  }
}

class ReferenceToFunction extends NodeImpl("referenceToFunction") with ToyReference {
  private val PredefinedIdentifiers = List("print", "println")

  lazy val target: Option[IdentifiedNode] = targetIn(_.functions)

  def predefined: Boolean = PredefinedIdentifiers.contains(identifier)
}

class ReferenceToValue extends NodeImpl("referenceToValue") with ToyReference
with ToyExpression with ReferenceToValueEvaluator with TypeCheck with ReferenceToValueTranslator {
  lazy val target: Option[IdentifiedNode] = targetIn(_.values)

  lazy val nodeType: Option[NodeType] = target.collect {
    case TypedNode(t) => t
  }

  def predefined = false
}
