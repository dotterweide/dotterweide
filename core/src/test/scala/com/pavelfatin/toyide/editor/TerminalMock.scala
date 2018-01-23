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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.Interval

class TerminalMock(offset0: Int, selection0: Option[Interval]) extends AbstractTerminal {
  offset = offset0

  selection = selection0

  def choose[T <: AnyRef](variants: Seq[T], query: String)(callback: T => Unit): Unit = ()

  def edit(s: String, title: String)(callback: Option[String] => Unit): Unit = ()
}