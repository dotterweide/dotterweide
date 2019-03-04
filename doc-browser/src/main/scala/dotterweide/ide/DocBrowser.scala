/*
 *  DocBrowser.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.ide

trait DocBrowser {
  def title: String

  def defer(thunk: => Unit): Unit

  def openURI(s: String): Unit

  def dispose(): Unit
}
