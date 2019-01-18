/*
 *  MockData.scala
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

import com.pavelfatin.toyide.lexer.Token
import com.pavelfatin.toyide.node.Node

class MockData extends Data {
  def text = ""

  def tokens: Seq[Token] = Seq.empty

  def structure: Option[Node] = None

  def errors: Seq[Error] = Seq.empty

  def pass: Pass = Pass.Text

  def hasNextPass = false

  def nextPass(): Unit = ()

  def compute(): Unit = ()

  def hasFatalErrors = false
}