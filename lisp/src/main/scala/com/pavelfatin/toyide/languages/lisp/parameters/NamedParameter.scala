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

private class NamedParameter(name: String) extends AbstractParameters {
  protected def bind0(argument: Expression): Map[String, Expression] =
    if (isPlaceholder) Map.empty else Map(name -> argument)

  private def isPlaceholder: Boolean = name == NamedParameter.Placeholder

  def symbols: Seq[String] = if (isPlaceholder) Nil else name :: Nil

  def presentation = throw new UnsupportedOperationException()
}

private object NamedParameter {
  private val Placeholder = "_"
}