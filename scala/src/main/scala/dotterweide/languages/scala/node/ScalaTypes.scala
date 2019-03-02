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

//object ScalaType {
//  def scalaDocAlias(p: PackageType, className: String): PackageType =
//    p.name match {
//      case "scala.collection.immutable" =>
//        className match {
//          case "List" => PackageType("scala")
//          case _ => p
//        }
//
//      case _ => p
//    }
//}

sealed trait ScalaType extends NodeType {
  /** scaladoc html path.
    *
    * @param leaf   whether this type is the leaf type
    *               in a composed type
    */
  def scalaDocPath(leaf: Boolean = true): Option[String]
}

//sealed trait PackageOrClassOrModuleType extends ScalaType

sealed trait ClassOrModuleType extends ScalaType {
  def prefix: ScalaType
  def name  : String

  def presentation: String = {
    val prefixPres = prefix.presentation
    if (prefixPres.isEmpty || prefixPres == "scala") name else prefixPres + "." + name
  }
}
case class PackageType(name: String) extends ScalaType {
  def presentation: String = name

  def scalaDocPath(leaf: Boolean): Option[String] = {
    val name1 = if (name.isEmpty) "scala" else name
    val s0    = (name1 + ".").replace('.', '/')
    val s1    = if (!leaf) s0 else s0 + "index.html"
    Some(s1)
  }
}

case class ModuleType(prefix: ScalaType, name: String) extends ClassOrModuleType {
  def scalaDocPath(leaf: Boolean): Option[String] = {
    val prefixPathOpt = prefix.scalaDocPath(leaf = false)
    prefixPathOpt.map { prefixPath =>
      val s0 = s"$prefixPath$name$$"
      val s1 = if (!leaf) s0 + "$" else s0 + ".html"
      s1
    }
  }
}

case class ClassType(prefix: ScalaType, name: String) extends ClassOrModuleType {
  def scalaDocPath(leaf: Boolean): Option[String] = {
    val prefixPathOpt = prefix.scalaDocPath(leaf = false)
    prefixPathOpt.map { prefixPath =>
      val s0 = s"$prefixPath$name"
      val s1 = if (!leaf) s0 + "$" else s0 + ".html"
      s1
    }
  }
}

// sealed trait MethodOrNullaryMethodType extends ScalaType

sealed trait MethodOrNullaryMethodType extends ScalaType {
  def ret: ScalaType

  def parent: ClassOrModuleType

  def scalaDocPathWithPoly(polyRep: String): Option[String]

  def scalaDocPath(leaf: Boolean): Option[String] = scalaDocPathWithPoly("")
}

case class MethodType(parent: ClassOrModuleType, name: String, params: ISeq[(String, ScalaType)], ret: ScalaType)
  extends MethodOrNullaryMethodType {

  def presentation: String = s"${parent.presentation}.$name"

  def scalaDocPathWithPoly(polyRep: String): Option[String] = {
    val paramPaths = params.map {
      case (pName, pTpe) => s"$pName:${pTpe.presentation}"
    }
    val parentPathOpt = parent.scalaDocPath()
    parentPathOpt.map { parentPath =>
      val retPath = ret.presentation
      s"$parentPath#$name$polyRep(${paramPaths.mkString(",")}):$retPath$polyRep"
    }
  }
}

case class NullaryMethodType(parent: ClassOrModuleType, name: String, ret: ScalaType)
  extends MethodOrNullaryMethodType {

  def presentation: String = s"${parent.presentation}.$name"

  def scalaDocPathWithPoly(polyRep: String): Option[String] = {
    val parentPathOpt = parent.scalaDocPath()
    parentPathOpt.map { parentPath =>
      val retPath = ret.presentation
      s"$parentPath#$name$polyRep:$retPath$polyRep"
    }
  }
}

case class PolyType(tParams: ISeq[ScalaType], res: ScalaType)
  extends ScalaType {

  private def tPresentation: String = tParams.map(_.presentation).mkString("[", ",", "]")

  def presentation: String = tPresentation + res.presentation // XXX TODO

  def scalaDocPath(leaf: Boolean): Option[String] =
    res match {
      case m: MethodOrNullaryMethodType => m.scalaDocPathWithPoly(tPresentation)
      case _ => None
    }
}

case class AbstractType(prefix: Option[ScalaType], name: String) extends ScalaType {
  def presentation: String = prefix.fold(name)(p => p.presentation + "#" + name)  // XXX TODO or dot?

  def scalaDocPath(leaf: Boolean): Option[String] = None
}