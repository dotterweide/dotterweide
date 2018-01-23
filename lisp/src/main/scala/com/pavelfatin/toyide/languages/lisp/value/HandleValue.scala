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

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream, FileOutputStream, InputStream, OutputStream}
import java.net.Socket

import com.pavelfatin.toyide.interpreter.Value
import com.pavelfatin.toyide.languages.lisp.LispType
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
    AbstractHandleValue.readAll(bufferedInput, builder, terminator)
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