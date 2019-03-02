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

import dotterweide.languages.scala.node.{ClassOrModuleType, ClassType, MethodType, ModuleType, PackageOrClassOrModuleType, PackageType, ScalaType}
import dotterweide.node.NodeType

import scala.reflect.internal.util.{Position => _Position}
import scala.tools.nsc.interactive.DotterweidePeek._

private trait TypeImpl {
  _: CompilerActor =>

  protected def runTypeAt(text0: String, offset0: Int): Option[NodeType] = {
    val offset    = offset0 + prelude.length
    val fullText  = prelude + text0 + postlude
    val srcFile   = c.newSourceFile(fullText)
    val pos       = _Position.offset(srcFile, offset)
    c.newTyperRun()
    reloadSource(c)(srcFile)
    val result = typedTreeAt(c)(pos)

//    println(s"TYPE TREE = $result | ${result.tpe} | ${result.getClass}")

    def prefixType(tpe: c.Type): Option[PackageOrClassOrModuleType] = {
      val sym = tpe.typeSymbol
      if (sym.isPackageClass /* isPackageObjectOrClass */) {
        val s     = tpe.safeToString
        val name  = if (s.endsWith(".type")) s.substring(0, s.length - 5) else s
        Some(PackageType(name))

      } else {
        tpe match {
          case c.TypeRef(pre, symC, _) if symC.isClass =>
            prefixType(pre).map { preTpe =>
              ClassType(preTpe, symC.nameString)
            }

          case c.SingleType(pre, symC) if symC.isModuleOrModuleClass =>
            prefixType(pre).map { preTpe =>
              ModuleType(preTpe, symC.nameString)
            }

          case _ => None
        }
      }
    }

    // XXX TODO --- what is the correct way to get package and class / object name?
    def detect(tpe: c.Type): Option[ScalaType] = tpe match {
      case c.TypeRef(pre, symC, _) if symC.isClass =>
        prefixType(pre).map { preTpe =>
          ClassType(preTpe, symC.nameString)
        }

      case c.SingleType(pre, symC) if symC.isModuleOrModuleClass =>
        prefixType(pre).map { preTpe =>
          ModuleType(preTpe, symC.nameString)
        }

      case c.NullaryMethodType(ret) =>
        println("TODO - NullaryMethodType")
        None

      case c.MethodType(params, ret) =>
        val paramsTpe = params.flatMap { p => detect(p.tpe).map { pTpe => p.nameString -> pTpe }}
        if (paramsTpe.size < params.size) None else {
          result match {
            case c.Select(qual, name) =>
              detect(qual.tpe).flatMap {
                case parent: ClassOrModuleType =>
                  detect(ret).map { retTpe =>
                    MethodType(parent, name.decoded, params = paramsTpe, ret = retTpe)
                  }

                case _ => None
              }
          }
        }
      case _ => None
    }

    detect(result.tpe)
  }
}
