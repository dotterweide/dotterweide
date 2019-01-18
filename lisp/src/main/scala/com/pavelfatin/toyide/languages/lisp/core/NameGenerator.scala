/*
 *  NameGenerator.scala
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

package com.pavelfatin.toyide.languages.lisp.core

import com.pavelfatin.toyide.languages.lisp.value.Environment

private class NameGenerator(environment: Environment) {
  private var aliases = Map[String, String]()

  def unique(name: String): String = aliases.getOrElse(name, {
    val alias = name + "_" + environment.nextId()
    aliases += name -> alias
    alias
  })
}
