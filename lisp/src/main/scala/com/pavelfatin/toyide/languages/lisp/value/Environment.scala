/*
 *  Environment.scala
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

package com.pavelfatin.toyide.languages.lisp.value

import com.pavelfatin.toyide.interpreter.Place

trait Environment {
  def lookup(name: String): Option[Expression]

  def locals: Map[String, Expression]

  def inFrame(place: Option[Place]): Environment

  def trace: Seq[Place]

  def addLocals(values: Map[String, Expression]): Environment

  def clearLocals: Environment

  def setGlobal(name: String, value: Expression): Unit

  def nextId(): Int

  def interrupt(message: String, place: Option[Place] = None): Nothing
}
