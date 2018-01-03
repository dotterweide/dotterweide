package de.sciss.scalalang

import scalariform.ScalaVersions

object ExploreScalariform {
  def main(args: Array[String]): Unit = run()

  def run(): Unit = {
    val src =
      """class Test {
        |  val x = // missing RHS
        |}""".stripMargin

    val tokens =
      scalariform.lexer.ScalaLexer.rawTokenise(src, forgiveErrors = true,
        scalaVersion = ScalaVersions.Scala_2_11.toString())

    println(s"---- TOKENS ----\n")
    tokens.foreach(println)
  }
}
