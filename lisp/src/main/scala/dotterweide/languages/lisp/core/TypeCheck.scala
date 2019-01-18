/*
 *  TypeCheck.scala
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

package dotterweide.languages.lisp.core

import dotterweide.Output
import dotterweide.interpreter.ValueType
import dotterweide.languages.lisp.LispType
import dotterweide.languages.lisp.LispType.{BooleanType, CharacterType, FunctionType, IntegerType, ListType, SymbolType}
import dotterweide.languages.lisp.value.{BooleanValue, Environment, Expression}

class TypeCheck(symbol: String, expectedType: LispType) extends CoreFunction(symbol) {
  def apply(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(ValueType(t)) => BooleanValue(t == expectedType)
    case _ => expected("expr", arguments, environment)
  }
}

object IsInteger    extends TypeCheck("integer?"  , IntegerType   )
object IsBoolean    extends TypeCheck("boolean?"  , BooleanType   )
object IsCharacter  extends TypeCheck("character?", CharacterType )
object IsSymbol     extends TypeCheck("symbol?"   , SymbolType    )
object IsFunction   extends TypeCheck("function?" , FunctionType  )
object IsList       extends TypeCheck("list?"     , ListType      )
