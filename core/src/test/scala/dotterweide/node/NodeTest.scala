/*
 *  NodeTest.scala
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

package dotterweide.node

import org.junit.Test
import org.junit.Assert._
import dotterweide.MockNode

class NodeTest {
  def node(children: NodeImpl*): NodeImpl = {
    val node = new MockNode()
    if(children.nonEmpty) node.children = children.toList
    node
  }

  @Test
  def elements(): Unit = {
    val a = node()
    val b = node()
    val c = node(a, b)
    val d = node()
    val e = node()
    val f = node(e)
    val g = node(d, c, f)
    assertEquals(Seq(g, d, c, a, b, f, e), g.elements)
  }

  @Test
  def nextSiblings(): Unit = {
    val n1 = node()
    val n2 = node()
    val n3 = node()

    n1.nextSibling = Some(n2)
    n2.nextSibling = Some(n3)

    assertEquals(Seq(n2, n3), n1.nextSiblings)
    assertEquals(Seq(n3), n2.nextSiblings)
    assertEquals(Seq(), n3.nextSiblings)
  }

  @Test
  def previousSiblings(): Unit = {
    val n1 = node()
    val n2 = node()
    val n3 = node()

    n3.previousSibling = Some(n2)
    n2.previousSibling = Some(n1)

    assertEquals(Seq(n2, n1), n3.previousSiblings)
    assertEquals(Seq(n1), n2.previousSiblings)
    assertEquals(Seq(), n1.previousSiblings)
  }
}