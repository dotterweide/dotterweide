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

import dotterweide.languages.scala.node.{AbstractType, ClassOrModuleType, ClassType, MethodType, ModuleType, NullaryMethodType, PackageType, PolyType, ScalaType}
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

    def prefixType(tpe: c.Type): Option[ScalaType] = {
      val sym = tpe.typeSymbol
      if (sym.isPackageClass /* isPackageObjectOrClass */) {
        val s     = tpe.safeToString
        // classes like `Predef` have a prefix that is just `"type"`.
        val name  = if (s.endsWith(".type")) s.substring(0, s.length - 5) else if (s == "type") "" else s
        Some(PackageType(name))

      } else {
        detect(tpe)
      }
    }

    // XXX TODO --- this is all quite messy

    def detect(tpe: c.Type): Option[ScalaType] = tpe match {
      case c.TypeRef(pre, symC, _) =>
        if (symC.isClass) {
          prefixType(pre).flatMap { preTpe =>
            val ct = ClassType(preTpe, symC.nameString)
            Some(ct)
          }
        } else if (symC.isAbstractType) {
          val preOpt = if (pre == c.NoPrefix) Some(None) else prefixType(pre).map(Some(_))
          preOpt.map { pre =>
            AbstractType(pre, symC.nameString)
          }
        } else {
          None
        }

      case c.SingleType(pre, symC) if symC.isModuleOrModuleClass =>
        prefixType(pre).map { preTpe =>
          ModuleType(preTpe, symC.nameString)
        }

      case c.NullaryMethodType(ret) =>
        result match {
          case c.Select(qual, name) =>
            detect(qual.tpe).flatMap {
              case parent: ClassOrModuleType =>
                detect(ret).map { retTpe =>
                  NullaryMethodType(parent, name.decoded, ret = retTpe)
                }

              case _ => None
            }

          case _ =>
            println(s"NullaryMethodType, no idea what to do with $result")
            None
        }

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

            case _ =>
              println(s"MethodType, no idea what to do with $result")
              None
          }
        }

      case c.PolyType(tParams, res) =>
        val tParamsTpe = tParams.flatMap { p => detect(p.tpe) } // .map { pTpe => p.nameString -> pTpe }}
        if (tParamsTpe.size < tParams.size) None else {
          val resTpeOpt = detect(res)
          resTpeOpt.map { resTpe =>
            PolyType(tParamsTpe, resTpe)
          }
        }

      case _ => None
    }

    // There is weird thing about nullary methods in that
    // `SinOsc.ar` for example gives the correct `v.Select` but
    // the `result.tpe` gives the _return_ type of `SinOsc.ar`, not
    // the `NullaryMethodType` - which we want. The following match
    // is aimed at rectifying that.
    result match {
      case c.Select(qual, name) =>
        result.tpe match {
          case c.TypeRef(_, _, _) | c.SingleType(_, _) =>
            // special casing
            detect(qual.tpe).flatMap {
              case parent: ClassOrModuleType =>
                detect(result.tpe).map { retTpe =>
                  NullaryMethodType(parent, name = name.decoded, ret = retTpe)
                }
              case _ => None
            }

          case _ => detect(result.tpe)
        }

      case _ => detect(result.tpe)
    }
  }
}
