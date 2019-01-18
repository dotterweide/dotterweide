/*
 *  Application.scala
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

package dotterweide

import java.awt.Dimension

import dotterweide.ide.MainFrame
import dotterweide.languages.lisp.LispLanguage
import dotterweide.languages.scala.ScalaLanguage
import dotterweide.languages.toy.ToyLanguage

import scala.swing.{SwingApplication, Window}

object Application extends SwingApplication {
  private val Languages = Seq(ScalaLanguage, ToyLanguage, LispLanguage)

  override def startup(args: Array[String]): Unit =
    selectLanguage().foreach(openMainFrame)

  private def selectLanguage(): Option[Language] = {
    val dialog = new LanguageDialog(Languages)
    open(dialog)
    dialog.selection
  }

  private def openMainFrame(language: Language): Unit = {
    val code = language.examples.headOption.fold("")(_.code)
    val frame = new MainFrame(language, code)
    frame.preferredSize = new Dimension(874, 696)
    open(frame)
  }

  private def open(window: Window): Unit = {
    window.pack()
    window.centerOnScreen()
    window.open()
  }
}
