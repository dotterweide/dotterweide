/*
 *  ScalaParser.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala

import dotterweide.document.Document
import dotterweide.editor.{Adviser, Async, Data}
import dotterweide.node.NodeType
import dotterweide.parser.Parser

import scala.concurrent.Future

trait ScalaParser extends Parser with Adviser {
  // XXX TODO temporary API
  // eventually, all `Language` instances should have a `Typer` method
  // or the `Adviser` should be extended by this method.
  def typeAsync(document: Document, data: Data, offset: Int)
               (implicit async: Async): Future[Option[NodeType]]
}
