/*
 *  TypeImpl.scala
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

import dotterweide.languages.scala.node.{ClassOrModuleType, ClassType, MethodType, ModuleType, ScalaType}
import dotterweide.node.NodeType

import scala.reflect.internal.util.{Position => _Position}
import scala.tools.nsc.interactive.DotterweidePeek._

private trait TypeImpl {
  _: CompilerActor =>

  protected def runType(text0: String, offset0: Int): Option[NodeType] = {
    val offset    = offset0 + prelude.length
    val fullText  = prelude + text0 + postlude
    val srcFile   = c.newSourceFile(fullText)
    val pos       = _Position.offset(srcFile, offset)
    c.newTyperRun()
    reloadSource(c)(srcFile)
    val result = typedTreeAt(c)(pos)

//    println(s"TYPE TREE = $result | ${result.tpe} | ${result.getClass}")

    def prefixString(tpe: c.Type): String = {
      val s = tpe.safeToString
      if (s.endsWith(".type")) s.substring(0, s.length - 5) else s
    }

    // XXX TODO --- what is the correct way to get package and class / object name?
    def detect(tpe: c.Type): Option[ScalaType] = tpe match {
      case c.TypeRef   (pre, sym, _) if sym.isClass               => Some(ClassType  (prefixString(pre), sym.nameString))
      case c.SingleType(pre, sym)    if sym.isModuleOrModuleClass => Some(ModuleType (prefixString(pre), sym.nameString))
      case c.MethodType(_, _) =>
        result match {
          case c.Select(qual, name) =>
            detect(qual.tpe).collect {
              case parent: ClassOrModuleType => MethodType(parent, name.decoded)
            }
        }
      case _ => None
    }

    detect(result.tpe)
  }
}
