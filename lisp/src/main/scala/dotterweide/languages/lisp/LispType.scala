/*
 *  LispType.scala
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

package dotterweide.languages.lisp

import dotterweide.node.AbstractNodeType

abstract sealed class LispType(presentation: String) extends AbstractNodeType(presentation)

object LispType {
  case object IntegerType   extends LispType("integer"  )
  case object BooleanType   extends LispType("boolean"  )
  case object CharacterType extends LispType("character")
  case object SymbolType    extends LispType("symbol"   )
  case object FunctionType  extends LispType("function" )
  case object ListType      extends LispType("list"     )
  case object HandleType    extends LispType("handle"   )
}