/*
 *  AdviserImpl.scala
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

import dotterweide.editor.{Adviser, Variant}

import scala.reflect.internal.util.{Position => _Position}

// XXX TODO --- this is mostly copy-and-paste from ScalaInterpreterPane, needs clean up
object AdviserImpl {
  private final case class Request(line: String, cursor: Int)
  private val NoRequest = Request("", -1)

  private object Completion {
    final case class Result(cursor: Int, candidates: List[Candidate])

    val NoResult = Result(-1, Nil)

    sealed trait Candidate /* extends Proxy */ {
      def name: String

      def fullString: String

      //    override def toString: String = fullString

      // def self: Any = stringRep
    }

    final case class Simple(name: String) extends Candidate {
      def fullString: String = name
    }

    final case class Def(name: String, info: String = "", isModule: Boolean = false) extends Candidate {
      def fullString: String = s"$name$info"

      override def toString: String = {
        val sInfo   = if (info.nonEmpty) s""", info = "$info"""" else ""
        val sIsMod  = if (isModule) ", isModule = true" else ""
        s"""Completion.$productPrefix("$name"$sInfo$sIsMod)"""
      }
    }
  }
}
private trait AdviserImpl {
  _: CompilerActor =>

  import AdviserImpl._

  private[this] var lastRequest = NoRequest
  private[this] var tabCount    = 0
  private[this] var lastCommonPrefixCompletion = Option.empty[String]

  protected def runComplete(text0: String, offset0: Int): Adviser.Result = {
    val offset    = offset0 + prelude.length
    val fullText  = (prelude + text0 + postlude).patch(offset, "_CURSOR_ ", 0)
    val srcFile   = c.newSourceFile(fullText)
    val pos       = _Position.offset(srcFile, offset)
    // c.askReset()
    c.newTyperRun()
    // minRunId_=(c)(c.currentRunId)
    val result = c.completionsAt(pos)

    log.debug(s"complete name = '${result.name}', delta = ${result.positionDelta}; size = ${result.results.size}")

    val request = Request(fullText, offset)
//    if (_tabCount >= 0) {
//      tabCount = _tabCount
//    } else
    if (request == lastRequest) {
      tabCount += 1
    } else {
      tabCount = 0
    }
    lastRequest = request

    val oldAPI = candidates(fullText, offset, result)
    val newAPI = oldAPI.candidates.map {
      case Completion.Simple(name)            => Variant(title = name, content = name, shift = 0)
      case df @ Completion.Def(name, info, _) => Variant(title = name, content = df.fullString, shift = if (info.isEmpty) 0 else -1)
    }
    result.name.decoded -> newAPI
  }

  private def longestCommonPrefix(xs: List[String]): String = xs match {
    case Nil      => ""
    case w :: Nil => w
    case _        =>
      // XXX TODO --- that does not look very efficient
      def lcp(ss: List[String]): String = {
        val w :: ws = ss
        if (w == "") ""
        else if (ws exists (s => s == "" || (s charAt 0) != (w charAt 0))) ""
        else w.substring(0, 1) + lcp(ss map (_ substring 1))
      }
      lcp(xs)
  }

  private def candidates(fullText: String, offset: Int, result: c.CompletionResult): Completion.Result /* Candidates */ = {
    import c.CompletionResult._

    def defStringCandidates(matching: List[c.Member], name: c.Name): Completion.Result /* Candidates */ = {
      val defStrings: List[Completion.Def] = for {
        member <- matching
        if member.symNameDropLocal == name
        sym <- member.sym.alternatives
        sugared = sym.sugaredSymbolOrSelf
      } yield {
        val tp = member.prefix.memberType(sym)

        val info: String =
          if (sugared.isType) {
            c.typeParamsString(tp)
          }
          else if (sugared.isModule) {
            // val modSym = sugared.asModule
            ""
          } else tp match {
            case c.PolyType(_, ret)       =>
              val info0 = c.typeParamsString(tp)
              // XXX TODO -- a bit of DRY
              val info1 = ret match {
                case c.MethodType(params, _)  => params.map(_.defString).mkString("(", ",", ")")
                case _                      => ""
              }
              info0 + info1

            case c.MethodType(params, _)  => params.map(_.defString).mkString("(", ",", ")")
            case _                        => ""
          }

        val n = sugared.nameString
        // val s = sugared.defStringSeenAs(tp)
        Completion.Def(n, info, isModule = sugared.isModule)
      }
      // XXX TODO : why is this used in the original code, but does not appear in the results?
      //        val empty: Completion.Candidate = new Completion.Candidate {
      //          def stringRep: String = ""
      //        } // ""
      val dist = defStrings.distinct
      //        println("distinct:")
      //        dist.foreach(println)
      Completion.Result(offset, /* empty :: */ dist)
    }

    val found = result match {
      case NoResults => Completion.NoResult // NoCandidates
      case r =>
        def shouldHide(m: c.Member): Boolean = {
          val isUniversal = c.definitions.isUniversalMember(m.sym)
          def viaUniversalExtensionMethod: Boolean = m match {
            case t: c.TypeMember if t.implicitlyAdded && t.viaView.info.params.head.info.bounds.isEmptyBounds => true
            case _ => false
          }

          (
            isUniversal && c.nme.isReplWrapperName(m.prefix.typeSymbol.name)
              || isUniversal && tabCount == 0 && r.name.isEmpty
              || viaUniversalExtensionMethod && tabCount == 0 && r.name.isEmpty
            )
        }

        val matching: List[c.Member] = r.matchingResults().filterNot(shouldHide)
        val tabAfterCommonPrefixCompletion = lastCommonPrefixCompletion.contains(fullText.substring(0, offset)) &&
          matching.exists(_.symNameDropLocal == r.name)

        val doubleTab = tabCount > 0 && matching.forall(_.symNameDropLocal == r.name)
        //          println(s"tabAfterCommonPrefixCompletion = $tabAfterCommonPrefixCompletion, doubleTab = $doubleTab")
        val mkDef = tabAfterCommonPrefixCompletion || doubleTab

        def tryCamelStuff: Completion.Result = {
          // Lenient matching based on camel case and on eliding JavaBean "get" / "is" boilerplate
          val camelMatches      : List[c.Member ] = r.matchingResults(c.CompletionResult.camelMatch(_)).filterNot(shouldHide)
          val memberCompletions : List[String   ] = camelMatches.map(_.symNameDropLocal.decoded).distinct.sorted

          def allowCompletion: Boolean =
            (memberCompletions.size == 1) ||
              c.CompletionResult.camelMatch(r.name).apply {
                val pre = longestCommonPrefix(memberCompletions)
                r.name.newName(pre)
              }

          val memberCompletionsF: List[Completion.Candidate] =
            memberCompletions.map(Completion.Simple)

          if (memberCompletions.isEmpty) {
            Completion.NoResult
          } else if (allowCompletion) {
            Completion.Result(offset - r.positionDelta, memberCompletionsF)
          } else {
            // XXX TODO : why is this used in the original code, but does not appear in the results?
            //              val empty: Completion.Candidate = new Completion.Candidate {
            //                def stringRep: String = ""
            //              } // ""
            Completion.Result(offset, /* empty :: */ memberCompletionsF)
          }
        }

        if (mkDef) {
          val attempt = defStringCandidates(matching, r.name)
          if (attempt.candidates.nonEmpty) attempt else tryCamelStuff

        } else if (matching.isEmpty) {
          tryCamelStuff

        } else if (matching.nonEmpty && matching.forall(_.symNameDropLocal == r.name)) {
          Completion.NoResult // don't offer completion if the only option has been fully typed already

        } else {
          // regular completion
          val memberCompletions: List[String] = matching.map { member =>
            val raw: c.Name = member.symNameDropLocal
            raw.decoded
          } .distinct.sorted
          val memberCompletionsF: List[Completion.Candidate] = memberCompletions.map(Completion.Simple)
          Completion.Result(offset - r.positionDelta, memberCompletionsF)
        }
    }
    lastCommonPrefixCompletion =
      if (found != Completion.NoResult && fullText.length >= found.cursor) {
        val pre = fullText.substring(0, found.cursor)
        val cs  = found.candidates.collect {
          case Completion.Simple(s) => s
        }
        val suf = longestCommonPrefix(cs)
        Some(pre + suf)
      } else {
        None
      }
    found
  }
}
