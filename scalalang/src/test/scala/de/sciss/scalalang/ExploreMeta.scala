package de.sciss.scalalang

object ExploreMeta {
  def main(args: Array[String]): Unit = run()

  def run(): Unit = {
    val sourceString =
      """class Test {
        |  val x = // missing RHS
        |}""".stripMargin

    import scala.meta._

    val (_, input) = dialects.Scala212(sourceString)
    val sourceP: Parsed[Source] = input.parse[Source]
    val source = sourceP.get
    println(source)
    printTokens(source.tokens)

    def printTokens(tk: Tokens): Unit = {
      println("---- TOKENS ----")
      tk.foreach { t =>
        println(f"${t.productPrefix}%10s: ${t.text}")
      }
    }

    val tokensP = input.tokenize
    val tokens = tokensP.get
    printTokens(tokens)
  }
}
