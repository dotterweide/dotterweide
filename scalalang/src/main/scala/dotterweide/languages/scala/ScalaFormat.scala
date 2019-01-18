/*
 *  ScalaFormat.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import dotterweide.formatter.Distance.Joint
import dotterweide.formatter.{Distance, Format}
import dotterweide.lexer.TokenKind

object ScalaFormat extends Format {
  def distanceFor(a: TokenKind, b: TokenKind): Distance = Joint

  def indentDeltaFor(a: TokenKind, b: TokenKind): Int = 0
}