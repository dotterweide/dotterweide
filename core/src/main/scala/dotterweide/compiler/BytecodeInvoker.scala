/*
 *  BytecodeInvoker.scala
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

package dotterweide.compiler

import java.io.{OutputStream, PrintStream}
import java.lang.reflect.InvocationTargetException

import dotterweide.Output

object BytecodeInvoker {
  @throws(classOf[InvocationException])
  def invoke(code: Array[Byte], name: String, output: Output): Unit = {
    val loader = new DynamicClassLoader()

    val mainClass   = loader.define(name, code)
    val constructor = mainClass.getConstructor(classOf[PrintStream])
    val instance    = constructor.newInstance(new PrintStream(new OutputAdapter(output)))
    val mainMethod  = mainClass.getMethod("run", classOf[Array[String]])

    try {
      mainMethod.invoke(instance, Array[String]())
    } catch {
      case e: InvocationTargetException =>
        val cause   = e.getCause
        val places  = cause.getStackTrace.filter(it => it.getClassName == name).map { it =>
          val method  = it.getMethodName
          val line    = it.getLineNumber
          if (method == "run") Place(None, line) else Place(Some(method), line)
        }
        throw InvocationException(cause.toString, places)
    }
  }

  private class DynamicClassLoader extends ClassLoader {
    def define(className: String, bytecode: Array[Byte]): Class[_] =
      super.defineClass(className, bytecode, 0, bytecode.length)
  }

  private class OutputAdapter(output: Output) extends OutputStream {
    def write(b: Int): Unit =
      output.print(b.toChar.toString)
  }
}
