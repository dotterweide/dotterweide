/*
 *  SaveAsAction.scala
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

package dotterweide.ide.action

import java.io.File
import javax.swing.KeyStroke
import javax.swing.filechooser.FileNameExtensionFilter

import dotterweide.ide.EditorTab

import scala.swing.{Action, Component, Dialog, FileChooser}

class SaveAsAction(title0: String, mnemonic0: Char, shortcut: String,
                           parent: Component, tab: EditorTab) extends Action(title0) {
  mnemonic = mnemonic0

  accelerator = Some(KeyStroke.getKeyStroke(shortcut))

  def apply(): Unit = {
    SaveAsAction.performOn(tab, parent)
  }
}

private object SaveAsAction {
  def performOn(tab: EditorTab, parent: Component, selection: Option[File] = None): Unit = {
    val chooser = new FileChooser()
    chooser.title = "Save As"
    chooser.fileFilter = new FileNameExtensionFilter(tab.fileType.name, tab.fileType.extension)
    selection.foreach(chooser.selectedFile = _)
    chooser.showSaveDialog(parent) match {
      case FileChooser.Result.Approve =>
        val file = toTarget(chooser.selectedFile, tab.fileType.extension)
        if (file.exists) {
          val result = Dialog.showConfirmation(parent,
            "File '%s' already exists.\nDo you want to overwrite it?".format(file.getName),
            "File already exists", Dialog.Options.YesNoCancel, Dialog.Message.Warning)
          result match {
            case Dialog.Result.Yes    => doSave(file, tab)
            case Dialog.Result.No     => performOn(tab, parent, Some(chooser.selectedFile))
            case Dialog.Result.Cancel =>
          }
        } else {
          doSave(file, tab)
        }
      case _ =>
    }
  }

  private def toTarget(file: File, extension: String): File = {
    val tail = ".%s".format(extension)
    val path = if (file.getName.endsWith(tail)) file.getPath else "%s%s".format(file.getPath, tail)
    new File(path)
  }

  private def doSave(file: File, tab: EditorTab): Unit = {
    IO.write(file, tab.text)
    tab.file = Some(file)
  }
}