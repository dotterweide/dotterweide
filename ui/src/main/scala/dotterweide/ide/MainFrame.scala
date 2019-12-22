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

package dotterweide.ide

import java.io.File

import dotterweide.Language
import dotterweide.editor.controller.FlashAction
import dotterweide.editor.painter.FlashPainter
import dotterweide.editor.{Editor, FlashImpl, FontSettings}
import dotterweide.ide.impl.{ConsoleImpl, LauncherImpl, PanelImpl}

import scala.swing.Frame
import scala.swing.event.WindowClosed

class MainFrame(language: Language, text: String, font: FontSettings = FontSettings.Default,
                stylingName: Option[String] = None, structure: Boolean = true, flash: Boolean = false)
  extends Frame {

  val console: Console = new ConsoleImpl(font)

  val panel: Panel = new PanelImpl(language, text0 = text, font = font, stylingName = stylingName,
    console = Some(console))

  panel.structureVisible = structure

  if (flash) {
    val emitter = new FlashImpl
    panel.editors.foreach { ed =>
      ed.addPainter (new FlashPainter(ed.painterContext, emitter))
      ed.addAction  (new FlashAction(ed.document, ed.terminal, emitter))
    }
  }

  reactions += {
    case WindowClosed(_) =>
      panel.dispose()
  }

  override def closeOperation(): Unit = {
    launcher.stop()
    dispose()
  }

  private[this] val launcher: Launcher  = new LauncherImpl

  private[this] val menu = {
    import panel.async
    new MainMenu(panel, this, panel.data, new NodeInterpreter(console),
      new NodeInvoker(console), launcher, console, panel.styling, language.examples)
  }

  private def updateTitle(file: Option[File], dirty: Boolean): Unit = {
    val name      = file.map(_.getName.replaceAll("\\.%s".format(language.fileType.extension), ""))
    val dirtyMark = if (dirty) "*" else ""
    title = "%s%s - Dotterweide 0.1.0-SNAPSHOT".format(dirtyMark, name.getOrElse("Untitled"))
  }

  private def focused(editor: Editor): Unit =
    menu.bindTo(editor.actions)

  focused(panel.currentEditor)

  panel.onChange {
    case Panel.EditorFocused(editor)  => focused(editor)
    case Panel.FileChanged(f)         => updateTitle(file = f, dirty = panel.isDirty)
    case Panel.DirtyChanged(d)        => updateTitle(file = panel.file, dirty = d)
    case _ =>
  }

  contents = panel.component

  menuBar = menu

  updateTitle(panel.file, panel.isDirty)
}