/*
 *  Extensions.scala
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

package com.pavelfatin.toyide

import scala.collection.generic.CanBuildFrom
import scala.reflect.{ClassTag, classTag}

object Extensions {
  import language.higherKinds

  private type CanBuildTo[Elem, CC[X]] = CanBuildFrom[Nothing, Elem, CC[Elem]]

  implicit class RichTraversable[CC[X] <: Traversable[X], A](val value: CC[A]) extends AnyVal {
    def filterBy[B](implicit m: ClassTag[B], cbf: CanBuildTo[B, CC]): CC[B] =
      value.filter(classTag[B].runtimeClass.isInstance(_)).map[B, CC[B]](_.asInstanceOf[B])(collection.breakOut)

    def findBy[B: ClassTag]: Option[B] =
      value.find(classTag[B].runtimeClass.isInstance(_)).map(_.asInstanceOf[B])

    def collectAll[B](pf: PartialFunction[A, B])(implicit cbf: CanBuildTo[B, CC]): Option[CC[B]] =
      if (value.forall(pf.isDefinedAt)) Some(value.collect(pf)(collection.breakOut)) else None
  }

  implicit class RichCharSequence(val chars: CharSequence) extends AnyVal {
    def count(p: Char => Boolean): Int = {
      var i = 0
      var n = 0
      while (i < chars.length) {
        if (p(chars.charAt(i))) n += 1
        i += 1
      }
      n
    }

    def take(n: Int): CharSequence =
      chars.subSequence(0, n)

    def subSequence(begin: Int): CharSequence =
      chars.subSequence(begin, chars.length)
  }
}