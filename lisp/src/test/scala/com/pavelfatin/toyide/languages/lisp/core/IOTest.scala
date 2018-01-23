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