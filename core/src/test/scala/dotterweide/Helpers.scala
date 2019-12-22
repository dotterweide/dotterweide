/*
 *  Helpers.scala
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

package dotterweide

import document.Document
import editor.{MockTerminal, Terminal}
import org.junit.Assert._
import dotterweide.Extensions._
import dotterweide.document.impl.DocumentImpl
import dotterweide.node.{Node, ReferenceNode}

object Helpers {
  def assertMatches[T](actual: T)(pattern: PartialFunction[T, Unit]): Unit =
    assertTrue("actual: " + actual.toString, pattern.isDefinedAt(actual))

  def assertNoProblemsIn(elements: Seq[Node]): Unit = {
    assertEquals(List.empty, elements.flatMap(_.token).filter(_.problem.isDefined).toList)
    assertEquals(List.empty, elements.filter(_.problem.isDefined).toList)
  }

  def assertNoUnresolvedIn(elements: Seq[Node]): Unit =
    assertEquals(List.empty, elements.filterBy[ReferenceNode].filter(_.unresolved))

  object Target {
    def unapply(node: Node): Option[(String, Int)] =
      Some((node.span.text, node.span.start))
  }

  object Text {
    def unapply(node: Node): Option[String] =
      Some(node.span.text)
  }

  object Offset {
    def unapply(node: Node): Option[Int] =
      Some(node.span.start)
  }

  object Line {
    def unapply(node: Node): Int =
      node.span.source.take(node.span.start).count(_ == '\n')
  }

  def parseDocument(code: String): (Document, Terminal) = {
    val offset = code.diff(Seq('[', ']')).indexOf('|')

    if(offset < 0) throw new IllegalArgumentException("Cursor position isn't specified: %s".format(code))

    val s = code.diff(Seq('|'))

    val selection = (s.indexOf('['), s.lastIndexOf(']')) match {
      case (a, b) if a >= 0 && b > a => Some(Interval(a, b - 1))
      case (a, b) if a < 0 && b >= 0 =>
        throw new IllegalArgumentException("Selection start isn't specified: %s".format(code))
      case (a, b) if a >= 0 && b < 0 =>
        throw new IllegalArgumentException("Selection end isn't specified: %s".format(code))
      case _ => None
    }

    val cleanCode = code.diff(Seq('[', ']', '|'))

    val document = new DocumentImpl(cleanCode)
    val terminal = new MockTerminal(offset, selection)

    document.onChange { _ =>
      assertInSync(document, terminal)
    }
    terminal.onChange { _ =>
      assertInSync(document, terminal)
    }

    (document, terminal)
  }

  private def assertInSync(document: Document, terminal: Terminal): Unit = {
    val interval = Interval(0, document.length)
    assertWithin(interval, terminal.offset)
    terminal.selection.foreach(assertWithin(interval, _))
    terminal.hover.foreach(assertWithin(interval, _))
    terminal.highlights.foreach(assertWithin(interval, _))
  }

  private def assertWithin(interval: Interval, it: Interval): Unit = {
    assertWithin(interval, it.start)
    assertWithin(interval, it.stop)
    assertTrue(it.start <= it.stop)
  }

  private def assertWithin(interval: Interval, i: Int): Unit =
    assertTrue("%d must be within %s".format(i, interval.toString), interval.touches(i))

  def formatDocument(document: Document, view: Terminal): String = {
    val selection   = view.selection.toList
    val insertions  = selection.map(_.stop -> ']') ++ Seq(view.offset -> '|') ++ selection.map(_.start -> '[')
    val builder     = new StringBuilder(document.text)
    for((i, c) <- insertions.sortBy(-_._1)) builder.insert(i, c)
    builder.toString()
  }
}