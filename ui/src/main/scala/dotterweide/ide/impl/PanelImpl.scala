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

package dotterweide.ide.impl

import java.awt.event.{ActionEvent, ActionListener, FocusAdapter, FocusEvent}
import java.io.File

import dotterweide.document.impl.DocumentImpl
import dotterweide.document.{Document, Location}
import dotterweide.editor.{Async, Data, Editor, EditorFactory, FontSettings, History, HistoryImpl, Pass}
import dotterweide.ide.{Console, DynamicStyling, Panel, StatusBar, StructureTab}
import dotterweide.{FileType, Language}
import javax.swing.Timer

import scala.collection.immutable.{Seq => ISeq}
import scala.swing.event.UIElementShown
import scala.swing.{BorderPanel, Component, Orientation, ScrollPane, SplitPane}

class PanelImpl(language          : Language,
                text0             : String              = "",
                font              : FontSettings        = FontSettings.Default,
                stylingName       : Option[String]      = None,
                console           : Option[Console]     = None,
                preferredGridSize : Option[(Int, Int)]  = None
               )
  extends Panel {

  // ---- fields ----

  private[this] var _disposed   = false
  private[this] var _split      = false
  private[this] var _file       = Option.empty[File]
  private[this] var _structure  = Option.empty[StructureTab]
  private[this] var _dirty      = false

  val history: History = new HistoryImpl

  val styling: DynamicStyling = {
    val res = new DynamicStyling(language.stylings)
    stylingName.foreach { name =>
      if (res.names.contains(name)) res.name = name
    }
    res
  }

  val document: Document = new DocumentImpl()

  private[this] val primaryEditor: Editor =
    EditorFactory.createEditorFor(document, language, history, styling, font, preferredGridSize)

  val data: Data = primaryEditor.data

  implicit val async: Async = primaryEditor.async

  private[this] val secondaryEditor : Editor = {
    EditorFactory.createEditorFor(document,
      primaryEditor.data, primaryEditor.errorHolder, language, history, styling, font, preferredGridSize)
  }

  val editors: ISeq[Editor] = primaryEditor :: secondaryEditor :: Nil

  private[this] var _currentEditor = primaryEditor

  // a non-repeating timer to invoke the next data pass with a delay
  // (the delay is adjusted in the data observer)
  private[this] val timer = {
    val t = new Timer(10, new ActionListener {
      def actionPerformed(e: ActionEvent): Unit =
        if (data.hasNextPass) {
          data.nextPass()
        }
    })
    t.setRepeats(false)
    t
  }

  val status: StatusBar = new StatusBarImpl

  private[this] val editorTab = new BorderPanel

  private[this] val bottomPanel = new BorderPanel {
    add(status.component, BorderPanel.Position.Center)
  }

  def setBottomRightComponent(c: Component): Unit = {
    bottomPanel.layout(c) = BorderPanel.Position.East
    bottomPanel.revalidate()
  }

  val component: Component = new BorderPanel {
    private[this] val center = console match {
      case Some(c)  =>
        val scroll            = new ScrollPane(c.component)
        val split             = new SplitPane(Orientation.Horizontal, editorTab, scroll)
        split.dividerLocation = 507
        split.resizeWeight    = 1.0
        split.border          = null
        split

      case None => editorTab
    }
    add(center     , BorderPanel.Position.Center)
    add(bottomPanel, BorderPanel.Position.South )

    reactions += {
      case UIElementShown(_) if !_disposed => timer.restart()
    }
  }

  // ---- methods ----

  def fileType: FileType = language.fileType

  def dispose(): Unit = if (!_disposed) {
    _disposed = true
    timer.stop()
    editors.foreach(_.dispose())
  }

  def currentEditor: Editor = _currentEditor

//  def addPainter    (p: Painter): Unit = editors.foreach(_.addPainter   (p))
//  def removePainter (p: Painter): Unit = editors.foreach(_.removePainter(p))
//
//  def addAction     (a: Action) : Unit = editors.foreach(_.addAction    (a))
//  def removeAction  (a: Action) : Unit = editors.foreach(_.removeAction (a))

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

  def text: String = primaryEditor.text

  def text_=(s: String): Unit = {
    primaryEditor.text = s
    history.clear()
  }

  def file: Option[File] = _file

  def file_=(file: Option[File]): Unit = if (_file != file) {
    _file = file
    notifyObservers(Panel.FileChanged(file))
  }

  def split: Boolean = _split

  def split_=(b: Boolean): Unit = if (_split != b) {
    _split = b
    updateLayout()
    val editor = if (split) secondaryEditor else primaryEditor
    editor.pane.requestFocusInWindow()
    notifyObservers(Panel.SplitChanged(b))
  }

  def isDirty: Boolean = _dirty

  def structureVisible: Boolean = _structure.isDefined

  def structureVisible_=(value: Boolean): Unit = if (value != _structure.isDefined) {
    _structure.foreach(_.dispose())
    _structure = if (!value) None else {
      val st = new StructureTab(primaryEditor.data, primaryEditor.terminal)
      Some(st)
    }
    updateLayout()
  }

  private def updateLayout(): Unit = {
    val editors = if (split) {
      val pane = new SplitPane(Orientation.Horizontal, primaryEditor.component, secondaryEditor.component)
      pane.resizeWeight = 0.5
      pane.border = null
      pane
    } else {
      primaryEditor.component
    }

    val pane = _structure match {
      case Some(st) =>
        val res = new SplitPane(Orientation.Vertical, editors, new ScrollPane(st))
        res.resizeWeight = 0.7
        res
      case None =>
        editors
    }

    editorTab.peer.removeAll()
    editorTab.layout(pane) = BorderPanel.Position.Center
    editorTab.revalidate()
  }

  // ---- constructor ----

  text = text0.filter(_ != '\r') // do not `.trim`, white space may be intentional

  data.onChange { _ =>
    if (data.hasNextPass) {
      val delay = if (data.pass == Pass.Text) 300 else 100
      timer.setInitialDelay(delay)
      timer.restart()
    }
  }

  history.onChange {
    val newDirty = history.canUndo
    if (_dirty != newDirty) {
      _dirty = newDirty
      notifyObservers(Panel.DirtyChanged(newDirty))
    }
  }

  editors.foreach(register)

  updateMessageFor      (primaryEditor)
  updateCaretLocationFor(primaryEditor)

  updateLayout()
}