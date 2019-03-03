/*
 *  Version.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.build

import scala.util.Try

object Version {
  def parse(s: String): Try[Version] = Try {
    val s0 = s.trim.split('.')
    if (s0.length != 3) throw new IllegalArgumentException(s"$s should have format epoch.major.minor")
    val Array(epoch, major, minor) = s0
    Version(epoch = epoch.toInt, major = major.toInt, minor = minor.toInt)
  }

  implicit object ordering extends Ordering[Version] {
    def compare(x: Version, y: Version): Int = {
      if      (x.epoch < y.epoch) -1 else if (x.epoch > y.epoch) +1
      else if (x.major < y.major) -1 else if (x.major > y.major) +1
      else if (x.minor < y.minor) -1 else if (x.minor > y.minor) +1
      else 0
    }
  }
}
case class Version(epoch: Int, major: Int, minor: Int) extends Ordered[Version] {
  /** Just `epoch.major` */
  def binaryCompatible: String = s"$epoch.$major"

  override def toString = s"$epoch.$major.$minor"

  def compare(that: Version): Int = Version.ordering.compare(this, that)
}