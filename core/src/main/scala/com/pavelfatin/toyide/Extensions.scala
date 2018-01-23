/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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