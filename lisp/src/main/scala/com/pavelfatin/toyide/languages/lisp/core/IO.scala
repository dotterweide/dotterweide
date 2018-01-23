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

package com.pavelfatin.toyide.languages.lisp.core

import java.io.{File, IOException}
import java.net.{ServerSocket, Socket, SocketException}
import java.util.concurrent.SynchronousQueue

import com.pavelfatin.toyide.Output
import com.pavelfatin.toyide.languages.lisp.value.{BooleanValue, CharacterValue, Environment, Expression, FileHandleValue, FunctionValue, HandleValue, IntegerValue, ListValue, SocketHandleValue, StringValue}

import scala.annotation.tailrec

abstract class IOFunction(name: String) extends CoreFunction(name) {
  final def apply(arguments: Seq[Expression], environment: Environment, output: Output): Expression = {
    try {
      apply0(arguments, environment, output)
    } catch {
      case e: IOException => error(e.getMessage, environment)
    }
  }

  protected def apply0(arguments: Seq[Expression], environment: Environment, output: Output): Expression
}

object Dir extends IOFunction("dir") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(StringValue(path)) =>
      val files = Option(new File(path).listFiles).map(_.toSeq).getOrElse(Nil)
      ListValue(files.map(file => StringValue(file.getName)))
    case _ => expected("path", arguments, environment)
  }
}

object Exists extends IOFunction("exists?") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(StringValue(filename)) =>
      BooleanValue(new File(filename).exists)
    case _ => expected("filename", arguments, environment)
  }
}

object Directory extends IOFunction("directory?") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): BooleanValue = arguments match {
    case Seq(StringValue(filename)) =>
      BooleanValue(new File(filename).isDirectory)
    case _ => expected("filename", arguments, environment)
  }
}

object Open extends IOFunction("open") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): FileHandleValue = arguments match {
    case Seq(StringValue(filename)) =>
      new FileHandleValue(new File(filename))
    case _ => expected("filename", arguments, environment)
  }
}

object Listen extends IOFunction("listen") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(IntegerValue(port), f: FunctionValue) =>
      val serverSocket = new ServerSocket(port)
      closeOnStopping(Thread.currentThread(), serverSocket)

      val sockets = new SynchronousQueue[Socket](true)
      acceptConnections(serverSocket, sockets)

      @tailrec
      def handleConnection(): Unit = {
        try {
          val socket = sockets.take()
          val socketHandle = new SocketHandleValue(socket)
          f.apply(Seq(socketHandle), environment, output)
        } catch {
          case _: IllegalMonitorStateException => return
        }
        handleConnection()
      }

      handleConnection()

      ListValue.Empty

    case _ => expected("port f", arguments, environment)
  }

  private def closeOnStopping(thread: Thread, serverSocket: ServerSocket): Unit = {
    val runnable = new Runnable() {
      override def run(): Unit = {
        thread.synchronized(thread.join())
        serverSocket.close()
      }
    }
    new Thread(runnable).start()
  }

  private def acceptConnections(serverSocket: ServerSocket, queue: SynchronousQueue[Socket]): Unit = {
    val runnable = new Runnable() {
      @tailrec
      def run(): Unit = {
        try {
          val socket = serverSocket.accept()
          queue.put(socket)
        } catch {
          case _: SocketException => return
        }
        run()
      }
    }
    new Thread(runnable).start()
  }
}

object Read extends IOFunction("read") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(handle: HandleValue) => read(handle, None)
    case Seq(handle: HandleValue, CharacterValue(terminator)) => read(handle, Some(terminator))
    case _ => expected("handle terminator?", arguments, environment)
  }

  private def read(handle: HandleValue, terminator: Option[Char]) = {
    val values = handle.read(terminator).map(CharacterValue)
    ListValue(values)
  }
}

object Write extends IOFunction("write") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(handle: HandleValue, StringValue(s)) =>
      handle.write(s.toSeq)
      ListValue.Empty
    case _ => expected("handle string", arguments, environment)
  }
}

object Flush extends IOFunction("flush") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(handle: HandleValue) =>
      handle.flush()
      ListValue.Empty
    case _ => expected("handle", arguments, environment)
  }
}

object Close extends IOFunction("close") {
  def apply0(arguments: Seq[Expression], environment: Environment, output: Output): ListValue = arguments match {
    case Seq(handle: HandleValue) =>
      handle.close()
      ListValue.Empty
    case _ => expected("handle", arguments, environment)
  }
}
