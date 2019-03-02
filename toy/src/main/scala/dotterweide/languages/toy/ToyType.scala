/*
 *  ToyType.scala
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

package dotterweide.languages.toy

import dotterweide.node.AbstractNodeType

abstract sealed class ToyType(presentation: String) extends AbstractNodeType(presentation)

object ToyType {
  case object StringType  extends ToyType("string")
  case object IntegerType extends ToyType("integer")
  case object BooleanType extends ToyType("boolean")
  case object VoidType    extends ToyType("void")
}