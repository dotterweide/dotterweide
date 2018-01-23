/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.interpreter.ValueType
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.languages.lisp.LispType.{BooleanType, CharacterType, FunctionType, IntegerType, ListType, SymbolType}
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, Environment, Expression}

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
