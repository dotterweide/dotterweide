/*
 *  Context.scala
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

package dotterweide.interpreter

import scala.collection.immutable.{Seq => ISeq}

trait Context {
  def get(local: Boolean, name: String): Value

  def put(local: Boolean, name: String, value: Value): Unit

  def update(local: Boolean, name: String, value: Value): Unit

  def inScope(action: => Unit): Unit

  def inFrame(place: Place)(action: => Unit): Option[Value]

  def dropFrame(value: Option[Value]): Unit

  def trace: ISeq[Place]
}