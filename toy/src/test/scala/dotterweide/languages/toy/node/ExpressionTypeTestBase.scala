/*
 *  ExpressionTypeTestBase.scala
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

import dotterweide.Helpers._
import dotterweide.languages.toy.ToyLexer
import dotterweide.languages.toy.parser.ExpressionParser
import dotterweide.node.{Expression, NodeType}
import org.junit.Assert._

trait ExpressionTypeTestBase {
  protected def assertTypeIs(code: String, nodeType: NodeType): Unit = {
    assert(code, Some(nodeType))
  }

  protected def assertNoType(code: String): Unit = {
    assert(code, None)
  }

  private def assert(code: String, expectedType: Option[NodeType]): Unit = {
    val exp = ExpressionParser.parse(ToyLexer.analyze(code))
    assertNoProblemsIn(exp.elements)
    exp match {
      case typed: Expression => assertEquals(expectedType, typed.nodeType)
      case _ => fail("Not an expression: " + exp.content)
    }
  }
}