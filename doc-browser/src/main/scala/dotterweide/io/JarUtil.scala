/*
 *  JarUtil.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.io

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileInputStream, FileOutputStream, OutputStream}
import java.util.jar.{JarEntry, JarInputStream}

// XXX TODO --- should go in a separate library

object JarUtil {
  /** Unpacks a JAR file into a directory. */
  def unpackFiles(jar: File, target: File): Map[String, File] = {
    val in = new JarInputStream(new BufferedInputStream(new FileInputStream(jar)))
    unpack[File](in) { entry =>
      val name  = entry.getName
      val f     = new File(target, name)
      val bsOpt = if (entry.isDirectory) {
        f.mkdirs()
        None
      } else {
        Some(new BufferedOutputStream(new FileOutputStream(f)))
      }
      (bsOpt, f)
    }
  }

  /** Unpacks a JAR input stream.
    *
    * @param in       the input stream containing the jar file contents
    * @param prepare  a function called for each jar entry. It should return
    *                 an output stream for each jar entry it wishes to write,
    *                 and always an opaque return value of type `A` which will
    *                 be found in the resulting map.
    * @tparam A       the value type of the resulting map
    * @return A map from jar entry names to values produced by `prepare`.
    */
  def unpack[A](in: JarInputStream)(prepare: JarEntry => (Option[OutputStream], A)): Map[String, A] = {
    import java.util.jar._

    val b = Map.newBuilder[String, A]

    try while ({
      val entry: JarEntry = in.getNextJarEntry
      (entry != null) && {
        val name = entry.getName
        val (bsOpt, f) = prepare(entry)
        bsOpt.foreach { bs =>
          try {
            val arr = new Array[Byte](1024)
            while ({
              val sz = in.read(arr, 0, 1024)
              (sz > 0) && { bs.write(arr, 0, sz); true }
            }) ()
          } finally {
            bs.close()
          }
        }
        b += name -> f
        true
      }
    }) () finally {
      in.close()
    }

    b.result()
  }
}
