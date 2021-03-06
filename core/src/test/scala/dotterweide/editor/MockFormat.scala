/*
 *  MockFormat.scala
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

package dotterweide.editor

import dotterweide.lexer.TokenKind
import dotterweide.formatter.{Distance, Format}
import dotterweide.formatter.Distance._

class MockFormat extends Format {
  def defaultTabSize: Int = 2

  def distanceFor(a: TokenKind, b: TokenKind): Distance = Joint

  def indentDeltaFor(a: TokenKind, b: TokenKind) = 0
}