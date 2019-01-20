/*
 *  AsyncImpl.scala
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

import java.awt.EventQueue
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, TimeoutException}
import scala.util.{Failure, Try}

class AsyncImpl(timeOut: Int = 500) extends Async {
  def await[A](f: Future[A]): Try[A] = try {
    Await.ready(f, Duration(timeOut, TimeUnit.MILLISECONDS))
    f.value.get
  } catch {
    case e: TimeoutException => Failure(e)
  }

  def defer(block: => Unit): Unit =
    if (EventQueue.isDispatchThread) block
    else EventQueue.invokeLater(new Runnable { def run(): Unit = block })

  implicit def executionContext: ExecutionContext = ExecutionContext.global
}
