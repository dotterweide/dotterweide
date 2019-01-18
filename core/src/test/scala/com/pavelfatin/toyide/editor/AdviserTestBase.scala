/*
 *  AdviserTestBase.scala
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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.lexer.Lexer
import com.pavelfatin.toyide.parser.Parser
import org.junit.Assert._

abstract class AdviserTestBase(lexer: Lexer, parser: Parser, adviser: Adviser) {
  protected def assertVariantsAre(code: String)(expected: String*): Unit = {
    assertVariants(code, _.toList == expected.toList)
  }

  protected def assertVariantsInclude(code: String)(expected: String*): Unit = {
    assertVariants(code, variants => expected.forall(variants.contains))
  }

  protected def assertVariantsExclude(code: String)(expected: String*): Unit = {
    assertVariants(code, variants => !expected.exists(variants.contains))
  }

  private def assertVariants(code: String, check: Seq[String] => Boolean): Unit = {
    val label = Adviser.Anchor
    val s = code.replaceFirst("\\|", label)
    val root = parser.parse(lexer.analyze(s))
    val anchor = root.elements.find(it => it.isLeaf && it.span.text.contains(label))
    anchor match {
      case Some(it) =>
        val variants = adviser.variants(root, it).map(_.title)
        assertTrue("Actual variants: " + variants.mkString(", "), check(variants))
      case None =>
        fail("No anchor found: %s".format(code))
    }
  }
}
