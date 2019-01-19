/*
 *  ToyExamples.scala
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

package dotterweide.languages.toy

import dotterweide.Example

import scala.collection.immutable.{Seq => ISeq}

object ToyExamples {
  val Euler1: String =
    """
      |// Project Euler - Problem 1
      |
      |// The sum of all the multiples of 3 or 5 below 1000.
      |
      |var sum: integer = 0;
      |
      |var i: integer = 0;
      |
      |while (i < 1000) {
      |  if (i % 3 == 0 || i % 5 == 0) {
      |    sum = sum + i;
      |  }
      |  i = i + 1;
      |}
      |
      |print(sum);
      |""".stripMargin

  val Euler2: String =
    """
      |// Project Euler - Problem 2
      |
      |// The sum of the even-valued terms in the Fibonacci
      |// sequence whose values do not exceed four million.
      |
      |var a: integer = 0;
      |var b: integer = 1;
      |
      |var sum: integer = 0;
      |
      |while (a <= 4000000) {
      |  if (a % 2 == 0) {
      |    sum = sum + a;
      |  }
      |  var t: integer = a;
      |  a = b;
      |  b = t + b;
      |}
      |
      |print(sum);
      |""".stripMargin

  val Euler6: String =
    """
      |// Project Euler - Problem 6
      |
      |// The difference between the sum of the squares of the first
      |// one hundred natural numbers and the square of the sum.
      |
      |var sum: integer = 0;
      |var sumOfSquares: integer = 0;
      |
      |var i: integer = 1;
      |
      |while (i <= 100) {
      |  var square: integer = i * i;
      |  sum = sum + i;
      |  sumOfSquares = sumOfSquares + square;
      |  i = i + 1;
      |}
      |
      |print(sum * sum - sumOfSquares);
      |""".stripMargin

  val FibonacciNumbers: String =
    """
      |// The first twenty terms in the Fibonacci sequence.
      |
      |def fib(a: integer, b: integer, c: integer): void = {
      |  if (c > 0) {
      |    print(a, " ");
      |    fib(b, a + b, c - 1);
      |  }
      |}
      |
      |fib(0, 1, 20);
      |""".stripMargin

  val PrimeNumbers: String =
    """
      |// The prime numbers whose values are less than one hundred.
      |
      |def isDivisible(n: integer, a: integer, b: integer): boolean = {
      |  var i: integer = a;
      |
      |  while (i * i <= b) {
      |    if (n % i == 0) {
      |      return true;
      |    }
      |    i = i + 1;
      |  }
      |
      |  return false;
      |}
      |
      |var i: integer = 2;
      |
      |while (i < 100) {
      |  if (!isDivisible(i, 2, i)) {
      |    print(i, " ");
      |  }
      |  i = i + 1;
      |}
      |""".stripMargin

  val MultiplicationTable: String =
    """
      |// Prints the multiplication table.
      |
      |var i: integer = 2;
      |
      |while (i < 10) {
      |  var j: integer = 2;
      |
      |  while (j < 10) {
      |    var k: integer = i * j;
      |    if (k < 10) {
      |      print(" ");
      |    }
      |    print(k, " ");
      |    j = j + 1;
      |  }
      |
      |  println();
      |  i = i + 1;
      |}
      |""".stripMargin

  val HighlightingDemo: String =
    """
      |// Highlighting demo (try different color schemes)
      |
      |// Unused variable
      |var name: string = "foo";
      |
      |// Expression type mismatch
      |while (12345) {}
      |
      |// Expression can be simplified
      |print(4 * 10 + 2);
      |
      |// Function cannot be resolved
      |unknown(true);
      |""".stripMargin

  val Exception: String =
    """
      |// Runtime exception and stack trace demo.
      |
      |def c(): void = {
      |  println(1 / 0);
      |}
      |
      |def b(): void = {
      |  c();
      |}
      |
      |def a(): void = {
      |  b();
      |}
      |
      |a();
      |""".stripMargin

  val StackOverflow: String =
    """
      |// Stack overflow demo.
      |
      |def foo(): void = {
      |  foo();
      |}
      |
      |foo();
      |""".stripMargin

  val Values: ISeq[Example] = List(
    Example("Project Euler 1", '1', Euler1),
    Example("Project Euler 2", '2', Euler2),
    Example("Project Euler 6", '6', Euler6),
    Example("Fibonacci Numbers", 'F', FibonacciNumbers),
    Example("Prime Numbers", 'P', PrimeNumbers),
    Example("Highlighting Demo", 'H', HighlightingDemo),
    Example("Exception", 'E', Exception),
    Example("Stack Overflow", 'S', StackOverflow))
}