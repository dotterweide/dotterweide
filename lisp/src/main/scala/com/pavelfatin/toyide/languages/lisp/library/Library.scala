/*
 * Copyright 2018 Pavel Fatin, https://pavelfatin.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pavelfatin.toyide.languages.lisp.library

import com.pavelfatin.toyide.ide.Console
import com.pavelfatin.toyide.languages.lisp.node.ProgramNode
import com.pavelfatin.toyide.languages.lisp.value.{Environment, EnvironmentImpl, Expression}
import com.pavelfatin.toyide.languages.lisp.{LispLexer, LispParser}
import com.pavelfatin.toyide.lexer.Token

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
