/*
 *  ToyTranslatable.scala
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

package dotterweide.languages.toy.compiler

import dotterweide.Extensions._
import dotterweide.compiler.{Translatable, TranslationException}
import dotterweide.node.Node

trait ToyTranslatable extends Translatable { self: Node =>
  protected def interrupt(message: String, values: Any*): Nothing =
    throw TranslationException(message.format(values: _*))

  protected def withLine(s: String): String = {
    val line = self.span.source.take(self.span.start).count(_ == '\n')
    ".line %d\n%s".format(line, s)
  }
}
