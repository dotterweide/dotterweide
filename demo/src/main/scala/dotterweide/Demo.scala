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
import java.nio.file.Files
import java.util.Locale

import de.sciss.submin.Submin
import dotterweide.build.Module
import dotterweide.demo.{LanguageDialog, ScalaDocLookUp}
import dotterweide.editor.ColorScheme
import dotterweide.ide.MainFrame
import dotterweide.languages.lisp.LispLanguage
import dotterweide.languages.scala.{ScalaExamples, ScalaLanguage}
import dotterweide.languages.toy.ToyLanguage

import scala.swing.event.WindowClosed
import scala.swing.{Swing, Window}

object Demo {
  private val Languages = List(
    new ScalaLanguage(prelude = "object Main {\n", postlude = "\n}", examples = ScalaExamples.Values,
      impliedPrefixes = List("scala.collection")),
    ToyLanguage,
    LispLanguage
  )

  sealed trait Laf
  object Laf {
    case object Default     extends Laf
    case object SubminLight extends Laf
    case object SubminDark  extends Laf
  }

  case class Config(language: Option[Language] = None, stylingName: Option[String] = None,
                    structure: Boolean = true, flash: Boolean = false, laf: Laf = Laf.Default)

  def main(args: Array[String]): Unit = {
    val default = Config()

    def findLanguage(name: String): Option[Language] = {
      val n          = name.toLowerCase(Locale.US)
      Languages.find(_.name.toLowerCase(Locale.US) == n)
    }

    val p = new scopt.OptionParser[Config]("Demo") {
      opt[String]('l', "language")
        .text(s"Select language (one of ${Languages.map(_.name).mkString(", ")})")
        .validate { v => if (findLanguage(v).isDefined) success else failure(s"Unknown language $v") }
        .action { (v, c) => c.copy(language = findLanguage(v)) }

      opt[String]('c', "colors")
        .text(s"Select color scheme name (one of ${ColorScheme.names.mkString(", ")})")
        .validate { v => if (ColorScheme.names.contains(v.capitalize)) success else failure(s"Unknown scheme $v") }
        .action { (v, c) => c.copy(stylingName = Some(v.capitalize)) }

      opt[Unit]("no-structure")
        .text("Do not show structure view")
        .action { (_, c) => c.copy(structure = false) }

      opt[Unit]("flash")
        .text("Demo flash function via shift-return")
        .action { (_, c) => c.copy(flash = true) }

      opt[Unit]("submin-light")
        .text("Use Submin light look-and-feel")
        .action { (_, c) => c.copy(laf = Laf.SubminLight) }

      opt[Unit]("submin-dark")
        .text("Use Submin dark look-and-feel")
        .action { (_, c) => c.copy(laf = Laf.SubminDark) }
    }
    p.parse(args, default).fold(sys.exit(1)) { config =>
      config.laf match {
        case Laf.SubminLight  => Submin.install(false)
        case Laf.SubminDark   => Submin.install(true )
        case Laf.Default      =>
      }

      Swing.onEDT(run(config))
    }
  }

  def run(config: Config): Unit = {
    val langOpt = config.language.orElse(selectLanguage())
    langOpt.foreach { language =>
      val code  = language.examples.headOption.fold("")(_.code)
      val frame = new MainFrame(language, code, stylingName = config.stylingName, structure = config.structure,
        flash = config.flash)
      frame.preferredSize = new Dimension(874, 696)
      open(frame)
      frame.listenTo(frame)
      frame.reactions += {
        case WindowClosed(_) => sys.exit()
      }

      // for Scala, we install the type-at-cursor action (ctrl-alt-D)
      language match {
        case sl: ScalaLanguage =>
          val docModule = Module("org.scala-lang", "scala-library", sl.scalaVersion)
          val cacheDir  = Files.createTempDirectory(null).toFile
          val dark      = config.laf == Laf.SubminDark
          new ScalaDocLookUp(sl, frame, docModule = docModule, cacheDir = cacheDir,
            useDarkScheme = dark, deleteOnExit = true)

        case _ =>
      }
    }
  }

  private def selectLanguage(): Option[Language] = {
    val dialog = new LanguageDialog(Languages)
    open(dialog)
    dialog.selection
  }

  private def open(window: Window): Unit = {
    window.pack()
    window.centerOnScreen()
    window.open()
  }
}
