/*
 *  DocumentImplAnchorTest.scala
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

package dotterweide.document

import org.junit.Test
import org.junit.Assert._

class DocumentImplAnchorTest {
  @Test
  def nothingLeftBias(): Unit = {
    assertBehaviorIs("[", "[") { _ =>
      Unit
    }
    assertBehaviorIs("foo[bar", "foo[bar") { _ =>
      Unit
    }
  }

  @Test
  def nothingRightBias(): Unit = {
    assertBehaviorIs("]", "]") { _ =>
      Unit
    }
    assertBehaviorIs("foo]bar", "foo]bar") { _ =>
      Unit
    }
  }

  @Test
  def insertLeftBias(): Unit = {
    assertBehaviorIs("foo[bar", "foSOMEo[bar") { document =>
      document.insert(2, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[bSOMEar") { document =>
      document.insert(4, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOMEbar") { document =>
      document.insert(3, "SOME")
    }
  }

  @Test
  def insertRightBias(): Unit = {
    assertBehaviorIs("foo]bar", "foSOMEo]bar") { document =>
      document.insert(2, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]bSOMEar") { document =>
      document.insert(4, "SOME")
    }
    assertBehaviorIs("foo]bar", "fooSOME]bar") { document =>
      document.insert(3, "SOME")
    }
  }

  @Test
  def removeLeftBias(): Unit = {
    assertBehaviorIs("foo[bar", "o[bar") { document =>
      document.remove(0, 2)
    }
    assertBehaviorIs("foo[bar", "foo[b") { document =>
      document.remove(4, 6)
    }
    assertBehaviorIs("foo[bar", "[bar") { document =>
      document.remove(0, 3)
    }
    assertBehaviorIs("foo[bar", "foo[") { document =>
      document.remove(3, 6)
    }
    assertBehaviorIs("foo[bar", "[") { document =>
      document.remove(0, 6)
    }
    assertBehaviorIs("foo[bar", "fo[ar") { document =>
      document.remove(2, 4)
    }
    assertBehaviorIs("foo[bar", "[ar") { document =>
      document.remove(0, 4)
    }
    assertBehaviorIs("foo[bar", "fo[") { document =>
      document.remove(2, 6)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(0, 0)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(6, 6)
    }
    assertBehaviorIs("foo[bar", "foo[bar") { document =>
      document.remove(3, 3)
    }
  }

  @Test
  def removeRightBias(): Unit = {
    assertBehaviorIs("foo]bar", "o]bar") { document =>
      document.remove(0, 2)
    }
    assertBehaviorIs("foo]bar", "foo]b") { document =>
      document.remove(4, 6)
    }
    assertBehaviorIs("foo]bar", "]bar") { document =>
      document.remove(0, 3)
    }
    assertBehaviorIs("foo]bar", "foo]") { document =>
      document.remove(3, 6)
    }
    assertBehaviorIs("foo]bar", "]") { document =>
      document.remove(0, 6)
    }
    assertBehaviorIs("foo]bar", "fo]ar") { document =>
      document.remove(2, 4)
    }
    assertBehaviorIs("foo]bar", "]ar") { document =>
      document.remove(0, 4)
    }
    assertBehaviorIs("foo]bar", "fo]") { document =>
      document.remove(2, 6)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(0, 0)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(6, 6)
    }
    assertBehaviorIs("foo]bar", "foo]bar") { document =>
      document.remove(3, 3)
    }
  }

  @Test
  def replaceLeftBias(): Unit = {
    assertBehaviorIs("foo[bar", "SOMEo[bar") { document =>
      document.replace(0, 2, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[bSOME") { document =>
      document.replace(4, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOME[bar") { document =>
      document.replace(0, 3, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOME") { document =>
      document.replace(3, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOM[E") { document =>
      document.replace(0, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "foS[OMEar") { document =>
      document.replace(2, 4, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOM[Ear") { document =>
      document.replace(0, 4, "SOME")
    }
    assertBehaviorIs("foo[bar", "foS[OME") { document =>
      document.replace(2, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "SOMEfoo[bar") { document =>
      document.replace(0, 0, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[barSOME") { document =>
      document.replace(6, 6, "SOME")
    }
    assertBehaviorIs("foo[bar", "foo[SOMEbar") { document =>
      document.replace(3, 3, "SOME")
    }
  }

  @Test
  def replaceRightBias(): Unit = {
    assertBehaviorIs("foo]bar", "SOMEo]bar") { document =>
      document.replace(0, 2, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]bSOME") { document =>
      document.replace(4, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOME]bar") { document =>
      document.replace(0, 3, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]SOME") { document =>
      document.replace(3, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOM]E") { document =>
      document.replace(0, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "foS]OMEar") { document =>
      document.replace(2, 4, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOM]Ear") { document =>
      document.replace(0, 4, "SOME")
    }
    assertBehaviorIs("foo]bar", "foS]OME") { document =>
      document.replace(2, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "SOMEfoo]bar") { document =>
      document.replace(0, 0, "SOME")
    }
    assertBehaviorIs("foo]bar", "foo]barSOME") { document =>
      document.replace(6, 6, "SOME")
    }
    assertBehaviorIs("foo]bar", "fooSOME]bar") { document =>
      document.replace(3, 3, "SOME")
    }
  }

  private def assertBehaviorIs(before: String, after: String)(action: Document => Unit): Unit = {
    val document = new DocumentImpl(before.diff(Seq('[', ']')))
    val (index, bias) = if (before.contains("[")) (before.indexOf('['), Bias.Left) else (before.indexOf(']'), Bias.Right)
    val anchor = document.createAnchorAt(index, bias)
    action(document)
    val char = if (bias == Bias.Left) '[' else ']'
    val actual = new StringBuilder(document.text).insert(anchor.offset, char).toString()
    assertEquals(after, actual)
  }
}