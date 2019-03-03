/*
 *  FileDownloadImpl.scala
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

import java.io.File
import java.nio.ByteBuffer

import dispatch.Defaults._
import dispatch._
import dotterweide.io.FileDownload.Progress
import io.netty.handler.codec.http.{HttpHeaderNames, HttpHeaders}
import org.asynchttpclient.handler.resumable.{ResumableAsyncHandler, ResumableRandomAccessFileListener}
import org.asynchttpclient.{AsyncHandler, ListenableFuture, Request}

import scala.concurrent.Promise
import scala.util.Try
import scala.util.control.NonFatal

class FileDownloadImpl(req: dispatch.Req, out: File, http: Http = Http.default)
  extends ResumableAsyncHandler with FileDownload {

  handler =>

  @volatile
  private[this] var _progress = Progress(0L, -1L)

  private[this] val raf = {
    if (out.isFile) out.delete()
    val res = new java.io.RandomAccessFile(out, "rw")
    if (res.length() > 0L) res.setLength(0L)
    res
  }

  private[this] var fileSize = -1L

  def progress: Progress = _progress

  // cf. https://github.com/dispatch/reboot/issues/119#issuecomment-289233891
  override def onThrowable(t: Throwable): Unit = {
    super.onThrowable(t)
    try raf.close() catch { case NonFatal(_) => }
  }

  override def onHeadersReceived(headers: HttpHeaders): AsyncHandler.State = {
    val res: AsyncHandler.State = super.onHeadersReceived(headers)
    if (res == AsyncHandler.State.CONTINUE) {
      val contentLengthHeader = headers.get(HttpHeaderNames.CONTENT_LENGTH) // "Content-Length"
      if (contentLengthHeader != null) {
        fileSize = java.lang.Long.parseLong(contentLengthHeader)
      }
    }
    res
  }

  setResumableListener(
    new ResumableRandomAccessFileListener(raf) {
      override def onBytesReceived(buffer: ByteBuffer): Unit = {
        super.onBytesReceived(buffer)
        if (fileSize > 0L) {
          val pos = raf.length()
          val p = Progress(pos, fileSize)
          _progress = p
          notifyObservers(p)
        }
      }
    }
  )

  private[this] val reqH: (Request, AsyncHandler[_]) = req > handler
  private[this] val lFut: ListenableFuture[_] = http.client.executeRequest(reqH._1, reqH._2) // XXX TODO --- this can block
  private[this] val pr    = Promise[Unit]()

  def status: Future[Unit] = pr.future

  lFut.addListener(
    new Runnable {
      def run(): Unit = pr.complete(Try[Unit](lFut.get()))
    },
    new java.util.concurrent.Executor {
      def execute(runnable: Runnable): Unit = executor.execute(runnable)
    }
  )

  /** Aborts the future with an InterruptedException */
  def abort(): Unit = lFut.abort(new InterruptedException("Download aborted"))
}