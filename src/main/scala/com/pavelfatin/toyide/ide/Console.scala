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

package com.pavelfatin.toyide.ide

import java.awt.Color

import com.pavelfatin.toyide.Output

trait Console extends Output {
  def print(s: String, color: Color): Unit

  def printLink(s: String, line: Int): Unit

  def clear(): Unit
}

object Console {
  object Null extends Console {
    def print(s: String): Unit = ()

    def print(s: String, color: Color): Unit = ()

    def printLink(s: String, line: Int): Unit = ()

    def clear(): Unit = ()
  }
}