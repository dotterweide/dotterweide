/*
 *  MockHandle.scala
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

package dotterweide.languages.lisp.examples

import dotterweide.languages.lisp.value.HandleValue

class MockHandle extends HandleValue {
  private var _input = Seq.empty[Char]

  private var _builder = new StringBuilder()

  private var _closed = false

  def read(terminator: Option[Char]): Seq[Char] = {
    val count = terminator.map(_input.indexOf(_) + 1).filter(_ > 0).getOrElse(_input.length)
    val (prefix, suffix) = _input.splitAt(count)
    _input = suffix
    prefix
  }

  def write(chars: Seq[Char]): Unit = {
    _builder ++= chars
  }

  def flush(): Unit = ()

  def close(): Unit = {
    _closed = true
  }

  def presentation = "MockHandle"

  def input: Seq[Char] = _input

  def input_=(s: String): Unit = {
    _input = s.toSeq
  }

  def output: String = _builder.toString().replace("\r\n", "\n")

  def closed: Boolean = _closed

  def reset(): Unit = {
    _input = Seq.empty
    _builder = new StringBuilder()
    _closed = false
  }
}
