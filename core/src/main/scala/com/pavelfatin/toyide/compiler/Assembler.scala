/*
 *  Assembler.scala
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

package com.pavelfatin.toyide.compiler

import java.io.{ByteArrayOutputStream, StringReader}

import com.pavelfatin.toyide.node.Node
import jasmin.ClassFile

object Assembler {
  def assemble(root: Node, name: String): Array[Byte] = {
    val code    = root.translate(name, new Labels()).toText(name)

    val input   = new StringReader(code)
    val output  = new ByteArrayOutputStream()

    val file    = new ClassFile()
    file.readJasmin(input, name, false)
    if (file.errorCount() > 0) throw new RuntimeException("Assembling error: %s".format(code))
    file.write(output)

    val bytes   = output.toByteArray

    output.close()
    input .close()

    bytes
  }
}