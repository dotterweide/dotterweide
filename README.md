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

## building

The project build with [sbt](http://www.scala-sbt.org/) with the main Scala version being 2.12.x.
To run the demo, use `sbt dotterweide-demo/run`. Note that the project in progress, there may still be
`???` lurking in the code. You can pass arguments to the demo run, for example:

    sbt 'dotterweide-demo/run --language scala --colors dark'

Note that for Scala 2.13.0-M5 compilation, you currently need a locally published version 0.2.7-SNAPSHOT
of [scalariform](https://github.com/Sciss/scalariform/tree/sciss_work).
