package de.sciss.scalalang

import com.pavelfatin.toyide.Example

object ScalaExamples {
  val Euler1: String =
    """
      |// Project Euler - Problem 1
      |
      |// The sum of all the multiples of 3 or 5 below 1000.
      |
      |var sum = 0
      |
      |var i = 0
      |
      |while (i < 1000) {
      |  if (i % 3 == 0 || i % 5 == 0) {
      |    sum += i
      |  }
      |  i += 1
      |}
      |
      |println(sum)
      |""".stripMargin

  val Euler2: String =
    """
      |// Project Euler - Problem 2
      |
      |// The sum of the even-valued terms in the Fibonacci
      |// sequence whose values do not exceed four million.
      |
      |var a = 0
      |var b = 1
      |
      |var sum = 0
      |
      |while (a <= 4000000) {
      |  if (a % 2 == 0) {
      |    sum += a
      |  }
      |  val t = a
      |  a = b
      |  b += t
      |}
      |
      |println(sum)
      |""".stripMargin

  val Euler6: String =
    """
      |// Project Euler - Problem 6
      |
      |// The difference between the sum of the squares of the first
      |// one hundred natural numbers and the square of the sum.
      |
      |var sum = 0
      |var sumOfSquares = 0
      |
      |var i = 1
      |
      |while (i <= 100) {
      |  val square = i * i
      |  sum += i
      |  sumOfSquares += square
      |  i += 1
      |}
      |
      |println(sum * sum - sumOfSquares)
      |""".stripMargin

  val FibonacciNumbers: String =
    """
      |// The first twenty terms in the Fibonacci sequence.
      |
      |def fib(a: Int, b: Int, c: Int): Unit =
      |  if (c > 0) {
      |    print(a)
      |    print(" ")
      |    fib(b, a + b, c - 1);
      |  } else {
      |    println()
      |  }
      |
      |fib(0, 1, 20)
      |""".stripMargin

  val PrimeNumbers: String =
    """
      |// The prime numbers whose values are less than one hundred.
      |
      |def isDivisible(n: Int, a: Int, b: Int): Boolean = {
      |  var i = a
      |
      |  while (i * i <= b) {
      |    if (n % i == 0) {
      |      return true;
      |    }
      |    i += 1
      |  }
      |
      |  false
      |}
      |
      |var i = 2
      |
      |while (i < 100) {
      |  if (!isDivisible(i, 2, i)) {
      |    print(i)
    |      print(" ")
      |  }
      |  i += 1
      |}
      |println()
      |""".stripMargin

  val MultiplicationTable: String =
    """
      |// Prints the multiplication table.
      |
      |var i = 2
      |
      |while (i < 10) {
      |  var j = 2
      |
      |  while (j < 10) {
      |    val k = i * j
      |    if (k < 10) {
      |      print(" ")
      |    }
      |    print(k)
      |    print(" ")
      |    j += 1
      |  }
      |
      |  println();
      |  i += 1
      |}
      |""".stripMargin

  val HighlightingDemo: String =
    """
      |// Highlighting demo (try different color schemes)
      |
      |// Unused variable
      |val name = "foo"
      |
      |// Expression type mismatch
      |while (12345) {}
      |
      |// Expression can be simplified
      |println(4 * 10 + 2)
      |
      |// Function cannot be resolved
      |unknown(true)
      |""".stripMargin

  val Exception: String =
    """
      |// Runtime exception and stack trace demo.
      |
      |def c(): Unit = println(1 / 0)
      |
      |def b(): Unit = c()
      |
      |def a(): Unit = b()
      |
      |a()
      |""".stripMargin

  val StackOverflow: String =
    """
      |// Stack overflow demo.
      |
      |def foo(): Unit = foo()
      |
      |foo()
      |""".stripMargin

  val Values = Seq(
    Example("Project Euler 1", '1', Euler1),
    Example("Project Euler 2", '2', Euler2),
    Example("Project Euler 6", '6', Euler6),
    Example("Fibonacci Numbers", 'F', FibonacciNumbers),
    Example("Prime Numbers", 'P', PrimeNumbers),
    Example("Highlighting Demo", 'H', HighlightingDemo),
    Example("Exception", 'E', Exception),
    Example("Stack Overflow", 'S', StackOverflow))
}