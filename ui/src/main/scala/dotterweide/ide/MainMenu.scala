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
import dotterweide.editor.{Data, EditorActions, Runner, Action => _Action}
import dotterweide.ide.action.{ColoringAction, ExampleAction, ExportToClassAction, InterpretAction, InvokeAction, NewAction, OpenAction, SaveAction, SaveAsAction, StopAction}
import javax.swing.KeyStroke

import scala.swing.event.Key
import scala.swing.{Action, CheckMenuItem, Component, Dimension, Frame, Menu, MenuBar, MenuItem, RadioMenuItem, Separator}

private class MainMenu(tab: EditorTab, frame: Frame, data: Data, interpreter: Runner, invoker: Runner, launcher: Launcher,
                       console: Console, coloring: DynamicColoring, examples: Seq[Example]) extends MenuBar {

  private val undo              = new MenuItem("")
  private val redo              = new MenuItem("")
  private val complete          = new MenuItem("")
  private val copy              = new MenuItem("")
  private val cut               = new MenuItem("")
  private val duplicateLine     = new MenuItem("")
  private val indentSelection   = new MenuItem("")
  private val unindentSelection = new MenuItem("")
  private val clearSelection    = new MenuItem("")
  private val format            = new MenuItem("")
  private val goToDeclaration   = new MenuItem("")
  private val moveLineDown      = new MenuItem("")
  private val moveLineUp        = new MenuItem("")
  private val optimize          = new MenuItem("")
  private val paste             = new MenuItem("")
  private val removeLine        = new MenuItem("")
  private val rename            = new MenuItem("")
  private val selectAll         = new MenuItem("")
  private val showUsages        = new MenuItem("")
  private val toggleLineComment = new MenuItem("")

  def bindTo(actions: EditorActions): Unit = {
    bind(complete         , actions.complete          )
    bind(copy             , actions.copy              )
    bind(cut              , actions.cut               )
    bind(duplicateLine    , actions.duplicateLine     )
    bind(indentSelection  , actions.indentSelection   )
    bind(unindentSelection, actions.unindentSelection )
    bind(clearSelection   , actions.clearSelection    )
    bind(format           , actions.format            )
    bind(goToDeclaration  , actions.goToDeclaration   )
    bind(moveLineDown     , actions.moveLineDown      )
    bind(moveLineUp       , actions.moveLineUp        )
    bind(optimize         , actions.optimize          )
    bind(paste            , actions.paste             )
    bind(removeLine       , actions.removeLine        )
    bind(rename           , actions.rename            )
    bind(selectAll        , actions.selectAll         )
    bind(showUsages       , actions.showUsages        )
    bind(toggleLineComment, actions.toggleLineComment )
    bind(undo             , actions.undo              )
    bind(redo             , actions.redo              )
  }

  private def bind(item: MenuItem, action: _Action): Unit =
    item.action = new ActionAdapter(action)

  contents += new Menu("File") {
    private val parent = Component.wrap(frame.peer.getRootPane)
    mnemonic = Key.F
    contents += new MenuItem(new NewAction    ("New"        , 'N', "ctrl pressed N"       , parent, tab))
    contents += new MenuItem(new OpenAction   ("Open..."    , 'O', "ctrl pressed O"       , parent, tab))
    contents += new MenuItem(new SaveAction   ("Save"       , 'S', "ctrl pressed S"       , parent, tab))
    contents += new MenuItem(new SaveAsAction ("Save As..." , 'A', "shift ctrl pressed S" , parent, tab))
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

  contents += new Menu("Edit") {
    mnemonic = Key.E

    contents += undo
    contents += redo
    contents += new Separator()
    contents += cut
    contents += copy
    contents += paste
    contents += new Separator()
    contents += selectAll
    contents += clearSelection
    contents += new Separator()
    contents += duplicateLine
    contents += removeLine
    contents += indentSelection
    contents += unindentSelection
  }

  contents += new Menu("Code") {
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
    contents += moveLineUp
    contents += moveLineDown
  }

  contents += new Menu("Run") {
    mnemonic = Key.R

    contents += new MenuItem(new InterpretAction("Interpret", 'I', "shift pressed F10", data, interpreter, launcher, console))
    contents += new MenuItem(new InvokeAction("Compile", 'C', "shift ctrl pressed F10", data, invoker, launcher, console))
    contents += new Separator()
    contents += new MenuItem(new StopAction("Stop", 'S', "ctrl pressed F2", launcher, console))
  }

  contents += new Menu("Examples") {
    mnemonic = Key.X
    contents ++= examples.map(it => new MenuItem(new ExampleAction(it.name, it.mnemonic, tab, it.code)))
  }

  contents += new Menu("Coloring") {
    mnemonic = Key.C
    contents ++= coloring.names.map(it => new RadioMenuItem(it) {
      action = new ColoringAction(coloring, it)

      coloring.onChange {
        updateSelection()
      }

      updateSelection()

      private def updateSelection(): Unit =
        selected = coloring.name == it
    })
  }

  contents += new Menu("Window") {
    mnemonic = Key.W

    contents += new CheckMenuItem("Split") {
      action = new Action(text) {
        mnemonic = 'S'
        accelerator = Some(KeyStroke.getKeyStroke("ctrl alt pressed S"))
        def apply(): Unit =
          tab.split = selected
      }
    }
  }

  contents += new Menu("Help") {
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
}