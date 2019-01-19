/*
 *  EnvironmentImpl.scala
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

package dotterweide.languages.lisp.value

import dotterweide.interpreter.{EvaluationException, Place}

import scala.collection.mutable

case class EnvironmentImpl private (locals: Map[String, Expression],
                                    globals: mutable.Map[String, Expression],
                                    ids: Iterator[Int],
                                    trace: List[Place]) extends Environment {
  def this(globals: Map[String, Expression]) {
    this(Map.empty, mutable.Map(globals.toSeq: _*), Iterator.from(0), List.empty)
  }

  def this() {
    this(Map.empty)
  }

  def lookup(name: String): Option[Expression] = locals.get(name).orElse(globals.get(name))

  def addLocals(values: Map[String, Expression]): EnvironmentImpl = copy(locals = locals ++ values)

  def clearLocals: EnvironmentImpl = copy(locals = Map.empty)

  def setGlobal(name: String, value: Expression): Unit = {
    globals(name) = value
  }

  def nextId(): Int = ids.next()

  def inFrame(place: Option[Place]): EnvironmentImpl = if (trace.size < EnvironmentImpl.MaxFrames) {
    copy(trace = place.getOrElse(Place(Some("Unknown"), -1)) :: trace)
  } else {
    throw EvaluationException("Stack overflow", trace)
  }

  def interrupt(message: String, place: Option[Place] = None) =
    throw EvaluationException(message, place.toList ::: trace)
}

object EnvironmentImpl {
  val MaxFrames = 200
}