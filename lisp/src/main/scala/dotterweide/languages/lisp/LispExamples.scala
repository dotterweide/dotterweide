/*
 *  LispExamples.scala
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

package dotterweide.languages.lisp

import dotterweide.Example
import dotterweide.languages.lisp.library.Library

import scala.collection.immutable.{Seq => ISeq}

object LispExamples {
  val WebServerCode: String = load("/examples/WebServer.lisp")

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }

  val Values: ISeq[Example] = List(
    Example("Web Server"        , 'W', WebServerCode),
    Example("Core Library"      , 'C', Library.CoreCode),
    Example("Function Library"  , 'F', Library.FunctionCode),
    Example("Arithmetic Library", 'A', Library.ArithmeticCode),
    Example("List Library"      , 'L', Library.ListCode))
}
