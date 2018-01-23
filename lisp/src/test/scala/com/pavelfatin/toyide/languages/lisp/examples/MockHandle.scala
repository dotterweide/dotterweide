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

package com.pavelfatin.toyide.languages.lisp.examples

import com.pavelfatin.toyide.languages.lisp.value.HandleValue

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
