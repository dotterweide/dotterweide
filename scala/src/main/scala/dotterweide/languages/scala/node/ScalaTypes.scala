/*
 *  ScalaType.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.languages.scala.node

import dotterweide.node.NodeType

sealed trait ScalaType extends NodeType

sealed trait ClassOrModuleType extends ScalaType {
  def pkg : String
  def name: String

  def presentation: String = s"$pkg.$name"
}
case class ModuleType(pkg: String, name: String) extends ClassOrModuleType
case class ClassType (pkg: String, name: String) extends ClassOrModuleType

case class MethodType(parent: ClassOrModuleType, name: String)
  extends ScalaType {

  def presentation: String = s"${parent.presentation}#$name"
}
