/*
 *  IOTest.scala
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

package com.pavelfatin.toyide.languages.lisp.core

import java.io.{File, FileWriter}

import com.pavelfatin.toyide.languages.lisp.library.LibraryTestBase
import org.junit.Assert._
import org.junit.Test

import scala.io.Source

class IOTest extends LibraryTestBase {
  @Test
  def exists(): Unit = {
    withTempFile { file =>
      assertValue("(exists? \"" + format(file.getPath) + "\")", "true")
      file.delete()
      assertValue("(exists? \"" + format(file.getPath) + "\")", "false")
    }
  }

  @Test
  def directory(): Unit = {
    withTempFile { file =>
      assertValue("(directory? \"" + format(file.getPath) + "\")", "false")
      assertValue("(directory? \"" + format(file.getParent) + "\")", "true")
    }
  }

  @Test
  def read(): Unit = {
    withTempFile { file =>
      write(file, "data")
      assertValue("(read (open \"" + format(file.getPath) + "\"))", "(\\d \\a \\t \\a)")
    }
  }

  @Test
  def write(): Unit = {
    withTempFile { file =>
      run("(let [file (open \"" + format(file.getPath) + "\")] (write file \"data\") (close file))")
      assertEquals("data", read(file))
    }
  }

  private def format(path: String) = path.replace('\\', '/')

  private def withTempFile(f: File => Unit): Unit = {
    val file = File.createTempFile("toyide", "test")
    file.deleteOnExit()
    try {
      f(file)
    } finally {
      file.delete()
    }
  }

  private def write(file: File, s: String): Unit = {
    val writer = new FileWriter(file)
    writer.write(s)
    writer.close()
  }

  private def read(file: File) = Source.fromFile(file).mkString
}