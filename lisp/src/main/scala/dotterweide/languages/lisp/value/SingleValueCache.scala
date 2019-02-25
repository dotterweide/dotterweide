/*
 *  SingleValueCache.scala
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

package dotterweide.languages.lisp.value

private class SingleValueCache[K, V] {
  private[this] var cachedPair: Option[(K, V)] = None

  def getOrUpdate(key: K)(value: => V): V = {
    cachedPair.filter(_._1 == key).map(_._2).getOrElse {
      val v = value
      cachedPair = Some((key, v))
      v
    }
  }
}
