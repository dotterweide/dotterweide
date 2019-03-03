/*
 *  Module.scala
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

object Module {
  def forScala(scalaVersion: Version)(groupId: String, baseId: String, version: Version): Module = {
    val artifactId = s"${baseId}_${scalaVersion.binaryCompatible}"
    Module(groupId = groupId, artifactId = artifactId, version = version)
  }
}
case class Module(groupId: String, artifactId: String, version: Version)