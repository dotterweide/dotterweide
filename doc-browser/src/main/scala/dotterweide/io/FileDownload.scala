/*
 *  FileDownload.scala
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

import dispatch.Future
import dotterweide.ObservableEvents
import dotterweide.io.FileDownload.Progress

// XXX TODO --- should go in a separate library

object FileDownload {
  /** Reports the progress of having download `written` bytes out of `total` */
  case class Progress(written: Long, total: Long) {
    /** Relative completion from zero to one. */
    def relative: Double = if (total <= 0L) 0.0 else written.toDouble / total
  }
}
/** The process of downloading a remote resource to local file. */
trait FileDownload extends ObservableEvents[Progress] {
  /** Completion and status of the download. */
  def status: Future[Unit]

  /** Download progress; `total` will be reported negative (and `relative` zero)
    * until the file size has been determined.
    */
  def progress: Progress

  /** Aborts the future with an `InterruptedException` */
  def abort(): Unit
}
