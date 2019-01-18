/*
 *  LiteralNode.scala
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

package dotterweide.languages.lisp.node

import dotterweide.languages.lisp.core.NewList
import dotterweide.languages.lisp.value.{BooleanValue, CharacterValue, Expression, IntegerValue}
import dotterweide.node.NodeImpl

trait LiteralNode extends ExpressionNode {
  override def toString: String = "%s(%s)".format(kind, span.text)
}

class IntegerLiteralNode extends NodeImpl("integer") with LiteralNode {
  def read0(source: String) = IntegerValue(text.toInt)
}

class BooleanLiteralNode extends NodeImpl("boolean") with LiteralNode {
  def read0(source: String) = BooleanValue(text.toBoolean)
}

class CharacterLiteralNode extends NodeImpl("character") with LiteralNode {
  def read0(source: String): CharacterValue = {
    val c = text.substring(1) match {
      case "return" => '\r'
      case "newline" => '\n'
      case "tab" => '\t'
      case "space" => ' '
      case s => s.charAt(0)
    }
    CharacterValue(c)
  }
}

class StringLiteralNode extends NodeImpl("string") with LiteralNode {
  def read0(source: String): Expression = {
    val s = text
    val characters = s.substring(1, s.length - 1)
      .replace("\\r", "\r")
      .replace("\\n", "\n")
      .replace("\\t", "\t")
      .replace("\\\\", "\\")
      .toSeq
    NewList(characters.map(CharacterValue))
  }
}
