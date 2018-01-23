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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.interpreter.{EvaluationException, Place}

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
    throw EvaluationException(message, place.toSeq ++ trace)
}

object EnvironmentImpl {
  val MaxFrames = 200
}