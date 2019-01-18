# Dotterweide

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/dotterweide/dotterweide-org?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/dotterweide/dotterweide.svg?branch=master)](https://travis-ci.org/dotterweide/dotterweide)

## statement

The dotterweide projects aims to develop an embeddable mini-IDE with support for the Scala programming language. Please refer to the
[organisational project](https://github.com/dotterweide/dotterweide-org) for further information.

This is the main repository, containing the editor and IDE. Its code was originally forked
from [ToyIDE](https://github.com/pavelfatin/toyide) by Pavel Fatin. Original code was released
under Apache License. This project - Dotterweide - is released under the
[GNU Lesser General Public License v2.1](https://www.gnu.org/licenses/lgpl-2.1.txt) or higher.
Over time, this project will diverge more and more from the original code base.

The project build with [sbt](http://www.scala-sbt.org/) with the main Scala version being 2.12.x.

Known issues:

- `sbt test` fails with several cases, also different between Scala 2.11 and 2.12.
  This must be either a race condition or dirty global state, because running, for
  example, `sbt 'testOnly com.pavelfatin.toyide.languages.lisp.library.CoreTest'`
  works without problem, although that spec fails when running all tests.
