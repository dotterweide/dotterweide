/*
 *  Library.scala
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

package dotterweide.languages.lisp.library

import dotterweide.ide.Console
import dotterweide.languages.lisp.node.ProgramNode
import dotterweide.languages.lisp.value.{Environment, EnvironmentImpl, Expression}
import dotterweide.languages.lisp.{LispLexer, LispParser}
import dotterweide.lexer.Token

class Library private (globals: Map[String, Expression]) {
  def createEnvironment(): Environment = new EnvironmentImpl(globals)

  def symbols: Set[String] = globals.keySet
}

object Library {
  val CoreCode      : String = load("/library/Core.lisp")
  val FunctionCode  : String = load("/library/Function.lisp")
  val ArithmeticCode: String = load("/library/Arithmetic.lisp")
  val ListCode      : String = load("/library/List.lisp")

  private var cachedInstance: Option[Library] = None

  private var busy = false

  def instance: Library = cachedInstance.getOrElse {
    val library = exclusively(new Library(Map.empty))(createLibrary())
    cachedInstance = Some(library)
    library
  }

  private def exclusively[A](default: => A)(create: => A): A = {
    if (busy) default else {
      busy = true
      val result = create
      busy = false
      result
    }
  }

  private def createLibrary(): Library = {
    val environment = new EnvironmentImpl()

    initialize(environment, "Core"      , CoreCode)
    initialize(environment, "Function"  , FunctionCode)
    initialize(environment, "Arithmetic", ArithmeticCode)
    initialize(environment, "List"      , ListCode)

    new Library(environment.globals.toMap)
  }

  private def load(file: String): String = {
    val source = io.Source.fromURL(getClass.getResource(file))
    val code = source.getLines().mkString("\n")
    source.close()
    code
  }

  private def initialize(environment: Environment, source: String, code: String): Environment =  {
    val program = parse(code)
    program.evaluate(source, environment, Console.Null)
    environment
  }
  
  private def parse(code: String): ProgramNode = {
    val tokens = analyze(code)

    val program = LispParser.parse(tokens.iterator).asInstanceOf[ProgramNode]

    val parserProblems = program.elements.filter(_.problem.isDefined)
    assert(parserProblems.isEmpty, parserProblems.mkString(", "))
    
    program
  }

  private def analyze(code: String): Seq[Token] = {
    val tokens = LispLexer.analyze(code).toSeq

    val lexerProblems = tokens.filter(_.problem.isDefined)
    assert(lexerProblems.isEmpty, lexerProblems.mkString(", "))
    
    tokens
  }
}
