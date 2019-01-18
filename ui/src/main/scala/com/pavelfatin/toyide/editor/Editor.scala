/*
 *  Editor.scala
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

package com.pavelfatin.toyide.editor

import com.pavelfatin.toyide.Observable
import com.pavelfatin.toyide.document.Document

import scala.swing.Component

trait Editor extends Observable {
  def document: Document

  def data: Data

  def holder: ErrorHolder

  def terminal: Terminal

  def component: Component

  def pane: Component

  var text: String

  def actions: EditorActions

  def message: Option[String]

  def dispose(): Unit
}