/*
 *  AbstractDocBrowser.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.ide

import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.text.FontSmoothingType
import javafx.scene.web.WebView

import scala.swing.Swing

abstract class AbstractDocBrowser extends DocBrowser {
  protected val baseTitle = "API Browser"

  protected val fxPanel = new JFXPanel

  var title: String

  private[this] var webView: WebView = _

  def defer(thunk: => Unit): Unit =
    if (Platform.isFxApplicationThread) thunk
    else Platform.runLater(new Runnable { def run(): Unit = thunk })

  def openURI(s: String): Unit = defer {
    webView.getEngine.load(s)
  }

  defer {
    val wv = new WebView
    wv.setFontSmoothingType(FontSmoothingType.GRAY)
    // wv.setContextMenuEnabled(true)
    // wv.setFontScale(1.2)
    // wv.setZoom(2.0)
    val scene = new Scene(wv)
    fxPanel.setScene(scene)

    val e = wv.getEngine
    import javafx.beans.value.{ChangeListener, ObservableValue}
    import javafx.concurrent.Worker.State
    e.getLoadWorker.stateProperty.addListener(new ChangeListener[State]() {
      override def changed(ov: ObservableValue[_ <: State], oldState: State, newState: State): Unit =
        if (newState == State.SUCCEEDED) {
          val value = e.getTitle
          Swing.onEDT {
            title = s"$baseTitle - $value"
          }
        }
    })

    webView = wv
  }
}
