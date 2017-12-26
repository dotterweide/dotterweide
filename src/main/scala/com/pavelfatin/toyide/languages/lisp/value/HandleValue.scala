/*
 * Copyright (C) 2014 Pavel Fatin <http://pavelfatin.com>
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

package com.pavelfatin.toyide.languages.lisp.value

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream, FileOutputStream, InputStream, OutputStream}
import java.net.Socket

import com.pavelfatin.toyide.interpreter.Value
import com.pavelfatin.toyide.languages.lisp.LispType
import com.pavelfatin.toyide.languages.lisp.value.AbstractHandleValue.readAll
import com.pavelfatin.toyide.node.NodeType

import scala.annotation.tailrec

trait HandleValue extends Value with EvaluableToSelf {
  def valueType: NodeType  = LispType.HandleType

  def read(terminator: Option[Char]): Seq[Char]

  def write(chars: Seq[Char]): Unit

  def flush(): Unit

  def close(): Unit
}

abstract class AbstractHandleValue(name: String, input: InputStream, output: OutputStream) extends HandleValue {
  private val bufferedInput   = new BufferedInputStream (input )
  private val bufferedOutput  = new BufferedOutputStream(output)

  def presentation: String = name

  def read(terminator: Option[Char]): List[Char] = {
    val builder = new StringBuilder()
    readAll(bufferedInput, builder, terminator)
    builder.toList
  }

  def write(chars: Seq[Char]): Unit = {
    val buffer = chars.map(_.toByte).toArray
    bufferedOutput.write(buffer)
  }

  def flush(): Unit = {
    bufferedOutput.flush()
  }

  def close(): Unit = {
    bufferedOutput.flush()

    bufferedInput.close()
    bufferedOutput.close()
  }
}

private object AbstractHandleValue {
  @tailrec
  private def readAll(input: InputStream, builder: StringBuilder, terminator: Option[Char]): Unit = {
      val b = input.read()
      if (b >= 0) {
        val char = b.toChar
        builder.append(char)
        if (!terminator.contains(char)) {
          readAll(input, builder, terminator)
        }
      }
    }
}

class FileHandleValue(file: File)
  extends AbstractHandleValue(file.getPath, new FileInputStream(file), new FileOutputStream(file, true))

class SocketHandleValue(socket: Socket)
  extends AbstractHandleValue(socket.getRemoteSocketAddress.toString, socket.getInputStream, socket.getOutputStream) {

  override def close(): Unit = {
    super.close()
    socket.close()
  }
}