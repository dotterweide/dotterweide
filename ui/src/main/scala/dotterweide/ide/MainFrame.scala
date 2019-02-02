/*
 *  MainFrame.scala
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

package dotterweide.ide

import java.awt.event.{ActionEvent, ActionListener, FocusAdapter, FocusEvent}

import dotterweide.Language
import dotterweide.document.Location
import dotterweide.editor.{Editor, EditorFactory, FontSettings, HistoryImpl, Pass}
import javax.swing.Timer

import scala.swing.event.{WindowClosed, WindowOpened}
import scala.swing.{BorderPanel, Component, Frame, Orientation, ScrollPane, SplitPane}

class MainFrame(language: Language, text: String, font: FontSettings = FontSettings.Default,
                stylingName: Option[String] = None)
  extends Frame {

  reactions += {
    case WindowOpened(_) =>
      timer.start()
    case WindowClosed(_) =>
      timer.stop()
      primaryEditor   .dispose()
      secondaryEditor .dispose()
  }

  override def closeOperation(): Unit = {
    launcher.stop()
    dispose()
  }

  private val history = new HistoryImpl()

  private val styling = {
    val res = new DynamicStyling(language.stylings)
    stylingName.foreach { name =>
      if (res.names.contains(name)) res.name = name
    }
    res
  }

  private val primaryEditor = EditorFactory.createEditorFor(language, history, styling, font)

  private lazy val secondaryEditor = {
    import primaryEditor.async
    EditorFactory.createEditorFor(primaryEditor.document,
      primaryEditor.data, primaryEditor.holder, language, history, styling, font)
  }

  private val data = primaryEditor.data

  // a non-repeating timer to invoke the next data pass with a delay
  // (the delay is adjusted in the data observer)
  private val timer = new Timer(10, new ActionListener {
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

  private val status    = new StatusBar
  private val tab       = new EditorTabImpl(language.fileType, history, primaryEditor, secondaryEditor)
  private val console   = new ConsoleImpl(font)
  private val launcher  = new LauncherImpl

  private val menu = {
    import primaryEditor.async
    new MainMenu(tab, this, primaryEditor.data, new NodeInterpreter(console),
      new NodeInvoker(console), launcher, console, styling, language.examples)
  }

  private def updateTitle(): Unit = {
    val name = tab.file.map(_.getName.replaceAll("\\.%s".format(language.fileType.extension), ""))
    title = "%s - Dotterweide 0.1.0-SNAPSHOT".format(name.getOrElse("Untitled"))
  }

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
        updateMessageFor(editor)
        updateCaretLocationFor(editor)
        menu.bindTo(editor.actions)
      }
    })
  }

  register(primaryEditor)
  register(secondaryEditor)

  updateMessageFor(primaryEditor)
  updateCaretLocationFor(primaryEditor)
  menu.bindTo(primaryEditor.actions)

  contents = new BorderPanel() {
    val split = new SplitPane(Orientation.Horizontal, tab, new ScrollPane(Component.wrap(console)))
    split.dividerLocation = 507
    split.resizeWeight    = 1.0d
    split.border          = null
    add(split , BorderPanel.Position.Center)
    add(status, BorderPanel.Position.South )
  }

  menuBar = menu

  tab.onChange {
    case EditorTab.FileChanged(_) => updateTitle()
    case _ =>
  }

  updateTitle()

  tab.text = text.filter(_ != '\r').trim
}