/*
 *  Async.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object Async {
  def apply(timeOut: Int = 500): Async = new AsyncImpl(timeOut)
}
trait Async {
  /** Await the future with an internal timeout. */
  def await[A](f: Future[A]): Try[A]

  def defer(block: => Unit): Unit

  implicit def executionContext: ExecutionContext
}
