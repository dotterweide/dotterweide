/*
 *  AssignmentParserTest.scala
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

package dotterweide.languages.toy.parser

import org.junit.Test

class AssignmentParserTest extends ParserTest(AssignmentParser) {
  @Test
  def normal(): Unit = {
    assertParsed("a = 1;",
      """
      assignment
        referenceToValue
          a
        EQ
        literal
          1
        SEMI
      """)
  }

  @Test
  def noSemi(): Unit = {
    assertParsed("a = 1",
      """
      assignment
        referenceToValue
          a
        EQ
        literal
          1
        error: leaf
      """)
  }

  @Test
  def noExpression(): Unit = {
    assertParsed("a =",
      """
      assignment
        referenceToValue
          a
        EQ
        error: leaf
      """)
  }
//
//  @Test
//  def noAssignment = assertParsed("a",
//"""
//assignment
// a
// error: leaf
//""")
//
//  @Test
//  def empty = assertParsed("",
//"""
//assignment
// error: leaf
//""")
}