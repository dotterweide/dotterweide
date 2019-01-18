/*
 *  InspectionTestBase.scala
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

package dotterweide.languages.toy.inspection

import dotterweide.Helpers._
import dotterweide.inspection.{Inspection, Mark}
import dotterweide.languages.toy.ToyLexer
import dotterweide.languages.toy.parser.ProgramParser

class InspectionTestBase(inspection: Inspection) {
  protected def marksIn(code: String): List[Mark] = {
    val elements = ProgramParser.parse(ToyLexer.analyze(code)).elements
    assertNoProblemsIn(elements)
    elements.flatMap(inspection.inspect).toList
  }
}