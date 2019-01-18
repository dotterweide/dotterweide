/*
 *  LibraryTestBase.scala
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

package dotterweide.languages.lisp.library

import dotterweide.languages.lisp.InterpreterTesting
import dotterweide.languages.lisp.value.Environment

trait LibraryTestBase extends InterpreterTesting {
  override def createEnvironment(): Environment = Library.instance.createEnvironment()
}

