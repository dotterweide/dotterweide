/*
 *  Logic.scala
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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, Environment, Expression}

object And extends CoreFunction("and", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = {
    val isFalse = arguments.toIterator.map(_.eval(environment, output)).exists {
      case BooleanValue(b) => !b
      case _ => expected("b1 b2 ...", arguments, environment)
    }
    BooleanValue(!isFalse)
  }
}

object Or extends CoreFunction("or", isLazy = true) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = {
    val isTrue = arguments.toIterator.map(_.eval(environment, output)).exists {
      case BooleanValue(b) => b
      case _ => expected("b1 b2 ...", arguments, environment)
    }
    BooleanValue(isTrue)
  }
}

object Not extends CoreFunction("not") {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(BooleanValue(b)) => BooleanValue(!b)
    case _ => expected("b", arguments, environment)
  }
}
