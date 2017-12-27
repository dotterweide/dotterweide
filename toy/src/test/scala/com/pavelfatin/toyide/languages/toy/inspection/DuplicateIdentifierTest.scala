/*
 * Copyright (C) 2011 Pavel Fatin <http://pavelfatin.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pavelfatin.toyide.languages.toy.inspection

import org.junit.Test
import com.pavelfatin.toyide.inspection.MarkData
import com.pavelfatin.toyide.Helpers._

class DuplicateIdentifierTest extends InspectionTestBase(DuplicateIdentifier) {
  @Test
  def functions(): Unit = {
    assertMatches(marksIn("def a(): void = {}; def b(): void = {}")) {
      case Nil =>
    }
  }

  @Test
  def functionsClash(): Unit = {
    val Message = DuplicateIdentifier.FunctionRedefinition("a")
    assertMatches(marksIn("def a(): void = {}; def a(p: integer): void = {}")) {
      case MarkData(Target("a", 24), Message) :: Nil =>
    }
  }

  @Test
  def variables(): Unit = {
    assertMatches(marksIn("var a: integer = 1; var b: integer = 2;")) {
      case Nil =>
    }
  }

  @Test
  def variablesClash(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; var a: boolean = true;")) {
      case MarkData(Target("a", 24), Message) :: Nil =>
    }
  }

  @Test
  def parameters(): Unit = {
    assertMatches(marksIn("def f(a: integer, b: integer): void = {}")) {
      case Nil =>
    }
  }

  @Test
  def parametersClash(): Unit = {
    val Message = DuplicateIdentifier.ParameterRedefinition("a")
    assertMatches(marksIn("def f(a: integer, a: boolean): void = {}")) {
      case MarkData(Target("a", 18), Message) :: Nil =>
    }
  }

  @Test
  def functionAndVariable(): Unit = {
    assertMatches(marksIn("def f(): void = {} var f: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def parameterAndVariable(): Unit = {
    assertMatches(marksIn("def f(a: integer): void = { var b: integer = 1; }")) {
      case Nil =>
    }
  }

  @Test
  def parameterAndVariableClash(): Unit = {
    val Message = DuplicateIdentifier.ParameterRedefinition("a")
    assertMatches(marksIn("def f(a: integer): void = { var a: integer = 1; }")) {
      case MarkData(Target("a", 32), Message) :: Nil =>
    }
  }

  @Test
  def variablesInsideFunction(): Unit = {
    assertMatches(marksIn("def f(): void = { var a: integer = 1; var b: integer = 2; }")) {
      case Nil =>
    }
  }

  @Test
  def variablesInsideFunctionClash(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("def f(): void = { var a: integer = 1; var a: integer = 2; }")) {
      case MarkData(Target("a", 42), Message) :: Nil =>
    }
  }

  @Test
  def parametersAndVariableInsideFunctionClash(): Unit = {
    val Message = DuplicateIdentifier.ParameterRedefinition("a")
    assertMatches(marksIn("def f(a: integer, a: integer): void = { var a: integer = 1; }")) {
      case MarkData(Target("a", 18), Message) :: MarkData(Target("a", 44), Message) :: Nil =>
    }
  }

  @Test
  def parameterAndVariablesInsideFunctionClash(): Unit = {
    val Message1 = DuplicateIdentifier.ParameterRedefinition("a")
    val Message2 = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("def f(a: integer): void = { var a: integer = 1; var a: integer = 2; }")) {
      case MarkData(Target("a", 32), Message1) :: MarkData(Target("a", 52), Message2) :: Nil =>
    }
  }

  @Test
  def multipleClash(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; var a: integer = 2; var a: integer = 3;")) {
      case MarkData(Target("a", 24), Message) :: MarkData(Target("a", 44), Message) :: Nil =>
    }
  }

  @Test
  def functionScope(): Unit = {
    assertMatches(marksIn("def f(): void = { var a: integer = 1; }; var a: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def ifScope(): Unit = {
    assertMatches(marksIn("if (true) { var a: integer = 1; }; var a: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def elseScope(): Unit = {
    assertMatches(marksIn("if (true) { var a: integer = 1; } else { var a: integer = 1; }; var a: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def whileScope(): Unit = {
    assertMatches(marksIn("while (true) { var a: integer = 1; }; var a: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def nestedScope(): Unit = {
    assertMatches(marksIn("if (true) { if (true) { var a: integer = 1; }; var a: integer = 1; }; var a: integer = 1;")) {
      case Nil =>
    }
  }

  @Test
  def sameLevelScope(): Unit = {
    assertMatches(marksIn("if (true) { var a: integer = 1; }; if (true) { var a: integer = 1; }")) {
      case Nil =>
    }
  }

  @Test
  def redefinitionInFunctionScope(): Unit = {
    assertMatches(marksIn("var a: integer = 1; def f(a: integer): void = {}")) {
      case Nil =>
    }
    assertMatches(marksIn("var a: integer = 1; def f(): void = { var a: integer = 1; }")) {
      case Nil =>
    }
  }

  @Test
  def redefinitionInIfScope(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; if (true) { var a: integer = 1; }")) {
      case MarkData(Target("a", 36), Message) :: Nil =>
    }
  }

  @Test
  def redefinitionInWhileScope(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; while (true) { var a: integer = 1; }")) {
      case MarkData(Target("a", 39), Message) :: Nil =>
    }
  }

  @Test
  def redefinitionInElseScope(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; if (true) {} else { var a: integer = 1; }")) {
      case MarkData(Target("a", 44), Message) :: Nil =>
    }
  }

  @Test
  def redefinitionInNestedControlScope(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("var a: integer = 1; if (true) { if (true) { var a: integer = 1; } }")) {
      case MarkData(Target("a", 48), Message) :: Nil =>
    }
  }

  @Test
  def redefinitionInNestedFunctionScope(): Unit = {
    assertMatches(marksIn("var a: integer = 1; def f(): void = { if (true) { var a: integer = 1; } }")) {
      case Nil =>
    }
  }

  @Test
  def redefinitionInControlScopeInsideFunctionScope(): Unit = {
    val Message = DuplicateIdentifier.VariableRedefinition("a")
    assertMatches(marksIn("def f(): void = { var a: integer = 1; if (true) { var a: integer = 1; } }")) {
      case MarkData(Target("a", 54), Message) :: Nil =>
    }
  }
}