/*
 *  ScalaDocLookUp.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.demo

import java.io.File

import dotterweide.build.Module
import dotterweide.editor.controller.LookUpTypeAction
import dotterweide.editor.{Async, Color, Editor}
import dotterweide.ide.{AbstractDocBrowser, DocUtil, MainFrame}
import dotterweide.languages.scala.ScalaLanguage
import dotterweide.languages.scala.node.ScalaType
import dotterweide.node.NodeType

import scala.concurrent.{ExecutionContext, Future}
import scala.swing.{Component, Dialog, Frame, GridPanel, Label, ProgressBar, Swing}
import scala.util.{Failure, Success}

class ScalaDocLookUp(language: ScalaLanguage, frame: MainFrame, docModule: Module, cacheDir: File,
                     deleteOnExit: Boolean, useDarkScheme: Boolean = false) {

  private[this] val panel       = frame.panel
  private[this] val baseDir     = DocUtil.defaultUnpackDir(cacheDir, docModule)
  private[this] val ready       = new File(baseDir, "ready")

  private[this] val ErrorColor  = new Color(200, 0, 0)
  private[this] val WarnColor   = new Color(160, 80, 0)

  private[this] implicit val async: Async = panel.async

  panel.editors.foreach { ed =>
    ed.addAction(new ActionImpl(ed))
  }

  private def printWarn(s: String): Unit =
    frame.console.print(s + "\n", WarnColor)

  private def printError(s: String): Unit =
    frame.console.print(s + "\n", ErrorColor)

  private def prepareJar()(implicit exec: ExecutionContext): Future[Unit] =
    if (ready.isFile) Future.successful(()) else {
      val (dl, futRes)    = DocUtil.downloadAndExtract(docModule, target = baseDir,
        darkCss = useDarkScheme, deleteOnExit = deleteOnExit)
      val progress        = new ProgressBar
      val progressDialog  = new Dialog(frame) {
        title = "Look up Documentation"
        contents = new GridPanel(2, 1) {
          vGap    = 2
          border  = Swing.EmptyBorder(2, 4, 2, 4)
          contents += new Label("Downloading API documentation...")
          contents += progress
        }
        pack().centerOnScreen()
        open()
      }
      dl.onChange { pr =>
        Swing.onEDT(progress.value = (pr.relative * 100).toInt)
      }
      futRes.onComplete(_ => Swing.onEDT(progressDialog.dispose()))
      futRes
    }

  private object browser extends AbstractDocBrowser {
    private lazy val webFrame: Frame = new Frame {
      title     = baseTitle
//      contents  = Component.wrap(fxPanel)
      bounds    = {
        val gc    = frame.peer.getGraphicsConfiguration
        val r     = gc.getBounds
        val x2    = r.x + r.width
        r.width   = math.min(r.width/2, 960)
        r.x       = x2 - r.width
        val h     = r.height
        r.height  = math.min(r.height, 960)
        r.y       = r.y + (h - r.height)/2
        r
      }
      open()
    }

    def dispose(): Unit = webFrame.dispose()

    def title: String = webFrame.title
    def title_=(value: String): Unit = {
      webFrame.title = value
      if (!webFrame.visible) webFrame.visible = true
    }
  }

  private def docForType(tpe: NodeType): Unit = tpe match {
    case sd: ScalaType =>
      sd.scalaDocPath() match {
        case Some(path) =>
          if (path.startsWith("scala/")) {
            import async.executionContext
            prepareJar().onComplete {
              case Success(_) =>
                ready.createNewFile()
                // XXX TODO --- `toURI` will escape the hash symbol; we should use URIs throughout
//                val docURI = (baseDir / path).toURI
                val docURI = "file://" + new File(baseDir, path).getPath
                browser.openURI(docURI)

              case Failure(ex) =>
                val msg = Option(ex.getMessage).getOrElse(ex.getClass.getSimpleName)
                ex.printStackTrace()
                printError(s"Failed to download javadoc jar: $msg")
            }

          } else {
            printWarn(s"Not part of the Scala library: ${sd.presentation}")
          }

        case None =>
          printWarn(s"Could not determine scala-doc path for ${sd.presentation}")
      }

    case _ =>
      printWarn("No scala type found")
  }

  private class ActionImpl(ed: Editor)
    extends LookUpTypeAction(ed.document, ed.terminal, ed.data, language.adviser) {

    override def run(tpeOpt: Option[NodeType]): Unit = {
      super.run(tpeOpt)
      tpeOpt.foreach(docForType)
    }
  }
}