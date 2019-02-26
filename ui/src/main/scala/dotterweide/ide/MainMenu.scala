/*
 *  MainMenu.scala
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

import dotterweide.Example
import dotterweide.editor.{Async, Data, EditorActions, Runner, Action => _Action}
import dotterweide.ide.action.{StylingAction, ExampleAction, ExportToClassAction, InterpretAction, InvokeAction, NewAction, OpenAction, SaveAction, SaveAsAction, StopAction}
import javax.swing.KeyStroke

import scala.swing.event.Key
import scala.swing.{Action, CheckMenuItem, Component, Dimension, Frame, Menu, MenuBar, MenuItem, RadioMenuItem, Separator}

private class MainMenu(panel: Panel, frame: Frame, data: Data, interpreter: Runner, invoker: Runner,
                       launcher: Launcher, console: Console, styling: DynamicStyling, examples: Seq[Example])
                      (implicit async: Async)
  extends MenuBar {

  private[this] val complete          = new MenuItem("")
  private[this] val copy              = new MenuItem("")
  private[this] val cut               = new MenuItem("")
  private[this] val fontEnlarge       = new MenuItem("")
  private[this] val fontShrink        = new MenuItem("")
  private[this] val fontReset         = new MenuItem("")
  private[this] val format            = new MenuItem("")
  private[this] val goToDeclaration   = new MenuItem("")
  private[this] val lineDuplicate     = new MenuItem("")
  private[this] val liveMoveDown      = new MenuItem("")
  private[this] val lineMoveUp        = new MenuItem("")
  private[this] val lineRemove        = new MenuItem("")
  private[this] val optimize          = new MenuItem("")
  private[this] val paste             = new MenuItem("")
  private[this] val redo              = new MenuItem("")
  private[this] val rename            = new MenuItem("")
  private[this] val selectAll         = new MenuItem("")
  private[this] val selectNone        = new MenuItem("")
  private[this] val selectionIndent   = new MenuItem("")
  private[this] val selectionUnindent = new MenuItem("")
  private[this] val showUsages        = new MenuItem("")
  private[this] val toggleLineComment = new MenuItem("")
  private[this] val undo              = new MenuItem("")

  def bindTo(actions: EditorActions): Unit = {
    bind(complete         , actions.complete          )
    bind(copy             , actions.copy              )
    bind(cut              , actions.cut               )
    bind(fontEnlarge      , actions.fontEnlarge       )
    bind(fontShrink       , actions.fontShrink        )
    bind(fontReset        , actions.fontReset         )
    bind(format           , actions.format            )
    bind(goToDeclaration  , actions.goToDeclaration   )
    bind(optimize         , actions.optimize          )
    bind(lineDuplicate    , actions.lineDuplicate     )
    bind(liveMoveDown     , actions.lineMoveDown      )
    bind(lineMoveUp       , actions.lineMoveUp        )
    bind(lineRemove       , actions.lineRemove        )
    bind(paste            , actions.paste             )
    bind(redo             , actions.redo              )
    bind(rename           , actions.rename            )
    bind(selectAll        , actions.selectAll         )
    bind(selectNone       , actions.selectNone        )
    bind(selectionIndent  , actions.selectionIndent   )
    bind(selectionUnindent, actions.selectionUnindent )
    bind(showUsages       , actions.showUsages        )
    bind(toggleLineComment, actions.toggleLineComment )
    bind(undo             , actions.undo              )
  }

  private def bind(item: MenuItem, action: _Action): Unit =
    item.action = new ActionAdapter(action)

  val mFile: Menu = new Menu("File") {
    private[this] val parent = Component.wrap(frame.peer.getRootPane)
    mnemonic = Key.F
    contents += new MenuItem(new NewAction    ("New"        , 'N', "ctrl pressed N"       , parent, panel))
    contents += new MenuItem(new OpenAction   ("Open..."    , 'O', "ctrl pressed O"       , parent, panel))
    contents += new MenuItem(new SaveAction   ("Save"       , 'S', "ctrl pressed S"       , parent, panel))
    contents += new MenuItem(new SaveAsAction ("Save As..." , 'A', "shift ctrl pressed S" , parent, panel))
    contents += new Separator()
    contents += new MenuItem(new ExportToClassAction("Export to Class...", 'E', data, parent))
    contents += new Separator()
    contents += new MenuItem(new Action("Exit") {
      mnemonic = 'X'
      def apply(): Unit = {
        frame.dispose()
      }
    })
  }

  contents += mFile

  val mEdit: Menu = new Menu("Edit") {
    mnemonic = Key.E

    contents += undo
    contents += redo
    contents += new Separator()
    contents += cut
    contents += copy
    contents += paste
    contents += new Separator()
    contents += selectAll
    contents += selectNone
    contents += new Separator()
    contents += lineDuplicate
    contents += lineRemove
    contents += selectionIndent
    contents += selectionUnindent
  }

  contents += mEdit

  val mCode: Menu = new Menu("Code") {
    mnemonic = Key.D

    contents += goToDeclaration
    contents += showUsages
    contents += new Separator()
    contents += complete
    contents += rename
    contents += new Separator()
    contents += toggleLineComment
    contents += new Separator()
    contents += optimize
    contents += format
    contents += new Separator()
    contents += lineMoveUp
    contents += liveMoveDown
  }

  contents += mCode

  val mRun: Menu = new Menu("Run") {
    mnemonic = Key.R

    contents += new MenuItem(new InterpretAction("Interpret", 'I', "shift pressed F10", data, interpreter, launcher, console))
    contents += new MenuItem(new InvokeAction("Compile", 'C', "shift ctrl pressed F10", data, invoker, launcher, console))
    contents += new Separator()
    contents += new MenuItem(new StopAction("Stop", 'S', "ctrl pressed F2", launcher, console))
  }

  contents += mRun

  val mExamples: Menu = new Menu("Examples") {
    mnemonic = Key.X
    contents ++= examples.map(it => new MenuItem(new ExampleAction(it.name, it.mnemonic, panel, it.code)))
  }

  contents += mExamples

  val mView: Menu = new Menu("View") {
    mnemonic = Key.V
    contents += new Menu("Color Scheme") {
      contents ++= styling.names.map(it => new RadioMenuItem(it) {
        action = new StylingAction(styling, it)

        styling.onChange {
          updateSelection()
        }

        updateSelection()

        private def updateSelection(): Unit =
          selected = styling.name == it
      })
    }

    contents += new Separator()
    contents += fontEnlarge
    contents += fontShrink
    contents += fontReset
    contents += new Separator()

    contents += new CheckMenuItem("Split Vertically") {
      action = new Action(text) {
        mnemonic = 'S'
        accelerator = Some(KeyStroke.getKeyStroke("ctrl alt pressed S"))
        def apply(): Unit =
          panel.split = selected
      }
    }
  }

  contents += mView

  val mHelp: Menu = new Menu("Help") {
    mnemonic = Key.H

    contents += new MenuItem(new Action("License") {
      mnemonic = 'L'

      def apply(): Unit = {
        val dialog = new InfoDialog(frame, "license.html", true)
        dialog.title = "License"
        dialog.preferredSize = new Dimension(600, 500)
        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.open()
      }
    })

    contents += new MenuItem(new Action("About") {
      mnemonic = 'A'
      accelerator = Some(KeyStroke.getKeyStroke("pressed F1"))

      def apply(): Unit = {
        val dialog = new InfoDialog(frame, "about.html", false)
        dialog.title = "About the program"
        dialog.pack()
        dialog.setLocationRelativeTo(frame)
        dialog.open()
      }
    })
  }

  contents += mHelp
}