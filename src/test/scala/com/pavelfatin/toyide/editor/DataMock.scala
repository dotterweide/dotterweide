/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.node.Node

class DataMock extends Data {
  def text = ""

  def tokens: Seq[Token] = Seq.empty

  def structure: Option[Node] = None

  def errors: Seq[Error] = Seq.empty

  def pass: Pass = Pass.Text

  def hasNextPass = false

  def nextPass(): Unit = ()

  def compute(): Unit = ()

  def hasFatalErrors = false
}