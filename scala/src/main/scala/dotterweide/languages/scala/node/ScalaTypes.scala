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

import scala.collection.immutable.{Seq => ISeq}

sealed trait ScalaType extends NodeType {
  /** scaladoc html path.
    *
    * @param leaf   whether this type is the leaf type
    *               in a composed type
    */
  def scalaDocPath(leaf: Boolean = true): Option[String]
}

sealed trait PackageOrClassOrModuleType extends ScalaType

sealed trait ClassOrModuleType extends PackageOrClassOrModuleType {
  def prefix: PackageOrClassOrModuleType
  def name  : String

  def presentation: String = s"${prefix.presentation}.$name"
}
case class PackageType(name: String) extends PackageOrClassOrModuleType {
  def presentation: String = name

  def scalaDocPath(leaf: Boolean): Option[String] = {
    val s0 = (name + ".").replace('.', '/')
    val s1 = if (!leaf) s0 else s0 + "index.html"
    Some(s1)
  }
}

case class ModuleType(prefix: PackageOrClassOrModuleType, name: String) extends ClassOrModuleType {
  def scalaDocPath(leaf: Boolean): Option[String] =
    prefix.scalaDocPath(leaf = false).map { prefixPath =>
      val s0 = s"$prefixPath$name$$"
      val s1 = if (!leaf) s0 + "$" else s0 + ".html"
      s1
    }
}

case class ClassType(prefix: PackageOrClassOrModuleType, name: String) extends ClassOrModuleType {
  def scalaDocPath(leaf: Boolean): Option[String] =
    prefix.scalaDocPath(leaf = false).map { prefixPath =>
      val s0 = s"$prefixPath$name"
      val s1 = if (!leaf) s0 + "$" else s0 + ".html"
      s1
    }
}

case class MethodType(parent: ClassOrModuleType, name: String, params: ISeq[(String, ScalaType)], ret: ScalaType)
  extends ScalaType {

  def presentation: String = s"${parent.presentation}.$name"

  def scalaDocPath(leaf: Boolean): Option[String] = {
    val paramPaths = params.map { case (pName, pTpe) => s"$pName:${pTpe.presentation}" }
    for {
      parentPath <- parent.scalaDocPath()
    } yield {
      val retPath = ret.presentation
      s"$parentPath#$name(${paramPaths.mkString(",")}):$retPath"
    }
  }
}
