/*
 *  ExpressionFormatTest.scala
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

package dotterweide.languages.toy.format

import dotterweide.languages.toy.parser._
import org.junit.Test

class ExpressionFormatTest extends FormatTestBase {
  @Test
  def group(): Unit = {
    assertFormatted("(1)", ExpressionParser, "(1)")
    assertFormatted("( 1)", ExpressionParser, "(1)")
    assertFormatted("(1 )", ExpressionParser, "(1)")
    assertFormatted("(  1  )", ExpressionParser, "(1)")
  }

  @Test
  def logicalOr(): Unit = {
    assertFormatted("1||2", ExpressionParser, "1 || 2")
    assertFormatted("1 || 2", ExpressionParser, "1 || 2")
    assertFormatted("1  ||  2", ExpressionParser, "1 || 2")
    assertFormatted("1||2||3", ExpressionParser, "1 || 2 || 3")
  }

  @Test
  def logicalAnd(): Unit = {
    assertFormatted("1&&2", ExpressionParser, "1 && 2")
    assertFormatted("1 && 2", ExpressionParser, "1 && 2")
    assertFormatted("1  &&  2", ExpressionParser, "1 && 2")
    assertFormatted("1&&2&&3", ExpressionParser, "1 && 2 && 3")
  }

  @Test
  def equality(): Unit = {
    assertFormatted("1==2", ExpressionParser, "1 == 2")
    assertFormatted("1 == 2", ExpressionParser, "1 == 2")
    assertFormatted("1  ==  2", ExpressionParser, "1 == 2")
    assertFormatted("1==2==3", ExpressionParser, "1 == 2 == 3")
    assertFormatted("1!=2", ExpressionParser, "1 != 2")
    assertFormatted("1 != 2", ExpressionParser, "1 != 2")
    assertFormatted("1  !=  2", ExpressionParser, "1 != 2")
    assertFormatted("1!=2!=3", ExpressionParser, "1 != 2 != 3")
  }

  @Test
  def relation(): Unit = {
    assertFormatted("1>2", ExpressionParser, "1 > 2")
    assertFormatted("1 > 2", ExpressionParser, "1 > 2")
    assertFormatted("1  >  2", ExpressionParser, "1 > 2")
    assertFormatted("1>2>3", ExpressionParser, "1 > 2 > 3")
    assertFormatted("1<2", ExpressionParser, "1 < 2")
    assertFormatted("1 < 2", ExpressionParser, "1 < 2")
    assertFormatted("1  <  2", ExpressionParser, "1 < 2")
    assertFormatted("1<2<3", ExpressionParser, "1 < 2 < 3")

    assertFormatted("1>=2", ExpressionParser, "1 >= 2")
    assertFormatted("1<=2", ExpressionParser, "1 <= 2")
  }

  @Test
  def addition(): Unit = {
    assertFormatted("1+2", ExpressionParser, "1 + 2")
    assertFormatted("1 + 2", ExpressionParser, "1 + 2")
    assertFormatted("1  +  2", ExpressionParser, "1 + 2")
    assertFormatted("1+2+3", ExpressionParser, "1 + 2 + 3")
    assertFormatted("1-2", ExpressionParser, "1 - 2")
    assertFormatted("1 - 2", ExpressionParser, "1 - 2")
    assertFormatted("1  -  2", ExpressionParser, "1 - 2")
    assertFormatted("1-2-3", ExpressionParser, "1 - 2 - 3")
  }

  @Test
  def multiplication(): Unit = {
    assertFormatted("1*2", ExpressionParser, "1 * 2")
    assertFormatted("1 * 2", ExpressionParser, "1 * 2")
    assertFormatted("1  *  2", ExpressionParser, "1 * 2")
    assertFormatted("1*2*3", ExpressionParser, "1 * 2 * 3")
    assertFormatted("1/2", ExpressionParser, "1 / 2")
    assertFormatted("1 / 2", ExpressionParser, "1 / 2")
    assertFormatted("1  /  2", ExpressionParser, "1 / 2")
    assertFormatted("1/2/3", ExpressionParser, "1 / 2 / 3")
  }

  @Test
  def modulus(): Unit = {
    assertFormatted("1%2", ExpressionParser, "1 % 2")
    assertFormatted("1 % 2", ExpressionParser, "1 % 2")
    assertFormatted("1  %  2", ExpressionParser, "1 % 2")
  }

  // TODO Can we obey these rules with token-based formatter?
//  @Test
//  def prefix() {
//    assertFormatted("-1", ExpressionParser, "-1")
//    assertFormatted("- 1", ExpressionParser, "-1")
//    assertFormatted("--1", ExpressionParser, "--1")
//    assertFormatted("- - 1", ExpressionParser, "--1")
//    assertFormatted("++1", ExpressionParser, "++1")
//    assertFormatted("+ + 1", ExpressionParser, "+ + 1")
//    assertFormatted("+1", ExpressionParser, "+1")
//    assertFormatted("+ 1", ExpressionParser, "+1")
//  }

  @Test
  def expression(): Unit = {
    assertFormatted("1+2*((3/4-2)>=5)<0||true&&6!=7", ExpressionParser,
      "1 + 2 * ((3 / 4 - 2) >= 5) < 0 || true && 6 != 7")

    assertFormatted("1  +  2  *  (  (  3  /  4  -  2  )  >=  5  )  <  0  ||  true  &&  6  !=  7", ExpressionParser,
      "1 + 2 * ((3 / 4 - 2) >= 5) < 0 || true && 6 != 7")
  }
}