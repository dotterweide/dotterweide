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

import java.io.{BufferedInputStream, BufferedOutputStream, File, FileOutputStream}

import dispatch.{Req, url}
import dotterweide.build.Module
import dotterweide.editor.controller.LookUpTypeAction
import dotterweide.editor.{Async, Color, Editor}
import dotterweide.ide.MainFrame
import dotterweide.io.{FileDownloadImpl, JarUtil}
import dotterweide.languages.scala.ScalaLanguage
import dotterweide.languages.scala.node.ScalaType
import dotterweide.node.NodeType
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.text.FontSmoothingType
import javafx.scene.web.WebView

import scala.concurrent.{ExecutionContext, Future, blocking}
import scala.language.implicitConversions
import scala.swing.{Component, Dialog, Frame, GridPanel, Label, ProgressBar, Swing}
import scala.util.{Failure, Success}

class ScalaDocLookUp(language: ScalaLanguage, frame: MainFrame, docModule: Module, cacheDir: File,
                     useDarkScheme: Boolean = false) {
  private implicit def FileOps(f: File): FileOps = new FileOps(f)
  private class FileOps(f: File) {
    def / (child: String): File = new File(f, child)
  }

  private[this] val panel       = frame.panel
  private[this] val baseDir     = cacheDir / docModule.groupId / docModule.artifactId / docModule.version.toString
  private[this] val styleDir    = baseDir / "lib"
  private[this] val ready       = baseDir / "ready"

  private[this] val ErrorColor  = new Color(200, 0, 0)
  private[this] val WarnColor   = new Color(160, 80, 0)

  private[this] implicit val async: Async = panel.async

  private def mkBaseUrl(repoBase: Req): Req =
    repoBase / docModule.groupId.replace('.', '/') / docModule.artifactId

//  private def mkMetaDataUrl(repoBase: Req): Req =
//    mkBaseUrl(repoBase) / "maven-metadata.xml"

  private def mkJavaDocUrl(repoBase: Req): Req = {
    val version = docModule.version.toString
    mkBaseUrl(repoBase) / version / s"${docModule.artifactId}-$version-javadoc.jar"
  }

  panel.editors.foreach { ed =>
    ed.addAction(new ActionImpl(ed))
  }

  private def copyResource(name: String, out: File): Unit = {
    val is = new BufferedInputStream(getClass.getClassLoader.getResourceAsStream(s"dotterweide/$name"))
    try {
      val os = new BufferedOutputStream(new FileOutputStream(out))
      try {
        var byte = 0
        while ({ byte = is.read(); byte != -1 }) {
          os.write(byte)
        }
      } finally  {
        os.close()
      }
    } finally {
      is.close()
    }
  }

  private def setStyle(): Unit = {
    val tpe = if (useDarkScheme) "dark" else "light"
    copyResource(s"index-$tpe.css"    , styleDir / "index.css")
    copyResource(s"template-$tpe.css" , styleDir / "template.css")
  }

  private def printWarn(s: String): Unit =
    frame.console.print(s + "\n", WarnColor)

  private def printError(s: String): Unit =
    frame.console.print(s + "\n", ErrorColor)

  private def prepareJar()(implicit exec: ExecutionContext): Future[Unit] =
    if (ready.isFile) Future.successful(()) else {
      val jarFile         = File.createTempFile("javadoc", ".jar")
      val repo            = url("https://repo1.maven.org/maven2")
      val req             = mkJavaDocUrl(repo)
      val dl              = new FileDownloadImpl(req = req, out = jarFile)
      val progress        = new ProgressBar
      val progressDialog  = new Dialog(frame) {
        title = "Look up Documentation"
        contents = new GridPanel(2, 1) {
          vGap    = 2
          border  = Swing.EmptyBorder(2, 4, 2, 4)
          contents += new Label("Downloading javadocs...")
          contents += progress
        }
        pack().centerOnScreen()
        open()
      }
      dl.onChange { pr =>
        Swing.onEDT(progress.value = (pr.relative * 100).toInt)
      }
      dl.status.onComplete(_ => Swing.onEDT(progressDialog.dispose()))
      dl.status.map { _ =>
        blocking {
          baseDir.mkdirs()
          val map = JarUtil.unpackFiles(jar = jarFile, target = baseDir)
          map.valuesIterator.foreach(_.deleteOnExit())
          jarFile.delete()
          setStyle()
          ready.createNewFile()
        }
        ()
      }
    }

  private[this] var docFrameOpt = Option.empty[(Frame, JFXPanel)]
  private[this] var webViewOpt  = Option.empty[WebView]

  private def browseURI(uri: String, title: String): Unit = {
    val (docFrame, fxPanel) = docFrameOpt.getOrElse {
      // cf. https://docs.oracle.com/javase/8/javafx/interoperability-tutorial/swing-fx-interoperability.htm
      val _fxPanel = new JFXPanel

      val f = new Frame {
        contents  = Component.wrap(_fxPanel)
        bounds    = {
          val r     = frame.peer.getGraphicsConfiguration.getBounds
          val x2    = r.x + r.width
          r.width   = math.min(r.width/2, 960)
          r.x       = x2 - r.width
          val h     = r.height
          r.height  = math.min(r.height, 720)
          r.y       = r.y + (h - r.height)/2
          r
        }
        open()
      }
      val tup = (f, _fxPanel)
      docFrameOpt = Some(tup)
      tup
    }

//    val name = {
//      val i = uri.indexOf('#')
//      val s = if (i < 0) uri else uri.substring(0, i)
//      val j = s.lastIndexOf('/')
//      val t = s.substring(j + 1)
//      val k = t.indexOf('$')
//      val m = if (k < 0) t.indexOf('.') else k
//      if (m < 0) t else t.substring(0, m)
//    }
    docFrame.title = s"API Browser - $title"
    if (!docFrame.visible) docFrame.visible = true

    javafx.application.Platform.runLater(Swing.Runnable {
      val web = webViewOpt.getOrElse {
        val wv = new WebView
        wv.setFontSmoothingType(FontSmoothingType.GRAY)
        // wv.setContextMenuEnabled(true)
        // wv.setFontScale(1.2)
        // wv.setZoom(2.0)
        webViewOpt = Some(wv)
        val scene = new Scene(wv)
        fxPanel.setScene(scene)
        wv
      }

      frame.console.print(uri + "\n")
      web.getEngine.load(uri)
    })
  }

  private def docForType(tpe: NodeType): Unit = tpe match {
    case sd: ScalaType =>
      sd.scalaDocPath() match {
        case Some(path) =>
          if (path.startsWith("scala/")) {
            // val url = s"https://www.scala-lang.org/api/${language.scalaVersion}/$path"
            // frame.console.print(url + "\n")
            // Desktop.getDesktop.browse(new URL(url).toURI)

            import async.executionContext

            prepareJar().onComplete {
              case Success(_) =>
                // XXX TODO --- `toURI` will escape the hash symbol; we should use URIs throughout
//                val docURI = (baseDir / path).toURI
                val docURI = "file://" + (baseDir / path).getPath
                browseURI(docURI, sd.presentation)

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

    override def run(tpeOpt: Option[NodeType]): Unit =
      tpeOpt.foreach(docForType)
  }
}