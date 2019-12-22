/*
 *  ErrorHolderImpl.scala
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

package dotterweide.editor.impl

import dotterweide.document.{AnchoredInterval, Document}
import dotterweide.editor.{Data, DataEvent, Error, ErrorHolder, ErrorsChanged, Pass}

import scala.collection.immutable.{Seq => ISeq}

class ErrorHolderImpl(document: Document, data: Data) extends ErrorHolder {
  private[this] var passToAnchoredErrors = Map.empty[Pass, ISeq[AnchoredError]]

  data.onChange {
    case DataEvent(pass, passErrors) =>
      val passAnchoredErrors = passToAnchoredErrors.getOrElse(pass, Nil)

      val previousPassErrors = passAnchoredErrors.map(_.toError)

      if (passErrors != previousPassErrors) {
        passAnchoredErrors.foreach(_.dispose())
        passToAnchoredErrors = passToAnchoredErrors.updated(pass, passErrors.map(new AnchoredError(_)).toVector)
        notifyObservers(ErrorsChanged(previousPassErrors, passErrors))
      }
    case _ =>
  }

  def errors: ISeq[Error] = passToAnchoredErrors.flatMap(_._2.map(_.toError)).toVector

  private class AnchoredError(error: Error) extends AnchoredInterval(document, error.interval) {
    def toError = Error(interval, error.message, error.decoration, error.fatal)
  }
}