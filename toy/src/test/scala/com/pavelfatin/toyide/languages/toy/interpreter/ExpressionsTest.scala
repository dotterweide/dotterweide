/*
 *  ExpressionsTest.scala
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

package com.pavelfatin.toyide.languages.toy.interpreter

import com.pavelfatin.toyide.interpreter.EvaluationException
import com.pavelfatin.toyide.languages.toy.ExpressionTestBase
import org.junit.{Assert, Test}

class ExpressionsTest extends ExpressionTestBase with InterpreterTesting {
  @Test
  def divisionByZero(): Unit = {
    try {
      run("print(1 / 0);")
    } catch {
      case EvaluationException(message, _) if message == "Division by zero" => return
    }

    Assert.fail("Expecting division by zero exception")
  }

  @Test
  def modulusWithZero(): Unit = {
    try {
      run("print(1 % 0);")
    } catch {
      case EvaluationException(message, _) if message == "Division by zero" => return
    }

    Assert.fail("Expecting division by zero exception")
  }
}