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

package com.pavelfatin.toyide.languages.lisp.parameters

import com.pavelfatin.toyide.languages.lisp.value.Expression

import scala.util.control.Exception._

trait AbstractParameters extends Parameters {
  final def bind(argument: Expression): Either[String, Map[String, Expression]] =
    catching(classOf[BindingException])
      .either(bind0(argument)).left.map(_.getMessage)

  protected def bind0(argument: Expression): Map[String, Expression]

  protected def error(message: String): Nothing = throw new BindingException(message)

  private class BindingException(message: String) extends Exception(message)
}
