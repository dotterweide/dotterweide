/*
 *  PanelImpl.scala
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

import java.awt.event.{ActionEvent, ActionListener, FocusAdapter, FocusEvent}
import java.io.File

import dotterweide.Language
import dotterweide.document.Location
import dotterweide.editor.{Async, Data, Editor, EditorFactory, FontSettings, History, HistoryImpl, Pass}
import javax.swing.Timer

import scala.swing.event.UIElementShown
import scala.swing.{BorderPanel, Component, Orientation, ScrollPane, SplitPane}

class PanelImpl(language: Language, text: String, font: FontSettings = FontSettings.Default,
                stylingName: Option[String] = None, console: Option[Console]) extends Panel {

  private[this] var disposed = false

  def dispose(): Unit = if (!disposed) {
    disposed = true
    timer           .stop()
    primaryEditor   .dispose()
    secondaryEditor .dispose()
  }

  val history: History = new HistoryImpl

  val styling: DynamicStyling = {
    val res = new DynamicStyling(language.stylings)
    stylingName.foreach { name =>
      if (res.names.contains(name)) res.name = name
    }
    res
  }

  private[this] val primaryEditor: Editor = EditorFactory.createEditorFor(language, history, styling, font)

  val data: Data = primaryEditor.data

  def file: Option[File] = editorTab.file

  def isDirty: Boolean = editorTab.isDirty

  implicit val async: Async = primaryEditor.async

  private[this] val secondaryEditor : Editor = {
    EditorFactory.createEditorFor(primaryEditor.document,
      primaryEditor.data, primaryEditor.holder, language, history, styling, font)
  }

  private[this] var _currentEditor = primaryEditor

  def currentEditor: Editor = _currentEditor

  // a non-repeating timer to invoke the next data pass with a delay
  // (the delay is adjusted in the data observer)
  private[this] val timer = new Timer(10, new ActionListener {
    def actionPerformed(e: ActionEvent): Unit =
      if (data.hasNextPass) {
        data.nextPass()
      }
  })

  timer.setRepeats(false)

  data.onChange { _ =>
    if (data.hasNextPass) {
      val delay = if (data.pass == Pass.Text) 300 else 100
      timer.setInitialDelay(delay)
      timer.restart()
    }
  }

  val status: StatusBar = new StatusBar

  val editorTab: EditorTab = new EditorTabImpl(language.fileType, history, primaryEditor, secondaryEditor)

  private def updateMessageFor(editor: Editor): Unit = {
    status.message = editor.message.mkString
  }

  private def updateCaretLocationFor(editor: Editor): Unit = {
    val Location(line, indent) = editor.document.toLocation(editor.terminal.offset)
    val selection = editor.terminal.selection.map(_.length.formatted("/%d")).mkString
    status.position = "%d:%d%s".format(line + 1, indent + 1, selection)
  }

  private def register(editor: Editor): Unit = {
    editor.onChange {
      if (editor.pane.hasFocus) updateMessageFor(editor)
    }
    editor.terminal.onChange { _ =>
      if (editor.pane.hasFocus) updateCaretLocationFor(editor)
    }
    // TODO use scala.swing listener
    editor.pane.peer.addFocusListener(new FocusAdapter {
      override def focusGained(e: FocusEvent): Unit = {
        _currentEditor = editor
        updateMessageFor(editor)
        updateCaretLocationFor(editor)
        notifyObservers(Panel.EditorFocused(editor))
      }
    })
  }

  register(primaryEditor)
  register(secondaryEditor)

  updateMessageFor      (primaryEditor)
  updateCaretLocationFor(primaryEditor)

  val component: Component = new BorderPanel() {
    private[this] val center = console match {
      case Some(c)  =>
        val split             = new SplitPane(Orientation.Horizontal, editorTab.component, new ScrollPane(c.component))
        split.dividerLocation = 507
        split.resizeWeight    = 1.0
        split.border          = null
        split

      case None => editorTab.component
    }
    add(center , BorderPanel.Position.Center)
    add(status, BorderPanel.Position.South )

    reactions += {
      case UIElementShown(_) if !disposed => timer.restart()
    }
  }

  editorTab.text = text.filter(_ != '\r').trim

  editorTab.onChange {
    case EditorTab.FileChanged  (f) => notifyObservers(Panel.FileOrDirtyChange(f, editorTab.isDirty))
    case EditorTab.DirtyChanged (d) => notifyObservers(Panel.FileOrDirtyChange(editorTab.file, d))
    case _ =>
  }
}