/*
 *  ProgramParserTest.scala
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

class ProgramParserTest extends ParserTest(ProgramParser) {
  @Test
  def empty(): Unit = {
    assertParsed("",
      """
      program
      """)
  }

  @Test
  def semi(): Unit = {
    assertParsed(";",
      """
      program
        empty
          SEMI
      """)
  }

  @Test
  def singleStatement(): Unit = {
    assertParsed("a = 1;",
      """
      program
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
  def error(): Unit = {
    assertParsed("foo",
      """
      program
        error: foo
      """)
  }

//  @Test
//  def errors = assertParsed("foo bar",
//"""
//program
//  error: foo
//  error: bar
//""")
}