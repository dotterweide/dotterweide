/*
 *  SymbolValue.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.{DelegateValue, Place}
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.languages.lisp.core.CoreFunction
import com.pavelfatin.toyide.node.NodeType

class SymbolValue private (val content: String, val place: Option[Place])
  extends DelegateValue[String] with Expression {

  def valueType: NodeType = LispType.SymbolType

  def eval(environment: Environment, output: Output): Expression = {
    CoreFunction.NameToFunction.get(content).orElse(environment.lookup(content))
      .getOrElse(environment.interrupt("Undefined symbol: " + content, place))
  }
}

object SymbolValue {
  def apply(name: String, place: Option[Place] = None): SymbolValue = new SymbolValue(name, place)

  def unapply(value: SymbolValue): Option[String] = Some(value.content)
}