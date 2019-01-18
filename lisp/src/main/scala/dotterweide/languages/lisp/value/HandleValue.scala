/*
 *  HandleValue.scala
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

package dotterweide.languages.lisp.value

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream, FileOutputStream, InputStream, OutputStream}
import java.net.Socket

import dotterweide.interpreter.Value
import dotterweide.languages.lisp.LispType
import dotterweide.node.NodeType

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