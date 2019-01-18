/*
 *  IO.scala
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

package com.pavelfatin.toyide.ide.action

import java.io.{BufferedWriter, File, FileWriter}

import scala.io.Source

private object IO {
  def write(file: File, text: String): Unit = {
    val s = text.replaceAll("\n", System.getProperty("line.separator"))
    val writer = new BufferedWriter(new FileWriter(file))
    try {
      writer.write(s)
      writer.flush()
    } finally {
      writer.close()
    }
  }

  def read(file: File): String = {
    val source = Source.fromFile(file)
    try {
      source.getLines().mkString("\n")
    } finally {
      source.close()
    }
  }
}