# Dotterweide

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/dotterweide/dotterweide-org?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://travis-ci.org/dotterweide/dotterweide.svg?branch=master)](https://travis-ci.org/dotterweide/dotterweide)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.sciss/dotterweide-core_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.sciss/dotterweide-core_2.12)

## statement

The dotterweide projects aims to develop an embeddable mini-IDE with support for the Scala programming language. Please refer to the
[organisational project](https://github.com/dotterweide/dotterweide-org) for further information.

This is the main repository, containing the editor and IDE. Its code was originally forked
from [ToyIDE](https://github.com/pavelfatin/toyide) by Pavel Fatin. Original code was released
under Apache License. This project - Dotterweide - is released under the
[GNU Lesser General Public License v2.1](https://www.gnu.org/licenses/lgpl-2.1.txt) or higher.
Over time, this project will diverge more and more from the original code base.

The demo is subject to GPL v3 due to the inclusion of [Submin](https://git.iem.at/sciss/Submin).

## linking

We are currently publishing the Scala related modules to Maven Central:

    "de.sciss" %% "dotterweide-core"        % v  // core API, few or no UI components
    "de.sciss" %% "dotterweide-ui"          % v  // Swing UI components
    "de.sciss" %% "dotterweide-scala"       % v  // Scala language support
    "de.sciss" %% "dotterweide-doc-browser" % v  // Building blocks for scaladoc/javadoc API browser

The current release version `v` is `"0.1.1"`.

## building

The project build with [sbt](http://www.scala-sbt.org/) with the main Scala version being 2.12.x.
To run the demo, use `sbt dotterweide-demo/run`. Note that the project in progress, there may still be
`???` lurking in the code. You can pass arguments to the demo run, for example:

    sbt 'dotterweide-demo/run --language scala --colors dark'
    
For interpreter integration, try `--flash` switch in the demo (shift-return flashes line or selection).

## documentation

Pretty much informal at this stage; see the `notes` directory and the -org project; ask in the Gitter channel.
There are [a few videos](https://peertube.social/video-channels/dotterweide/videos).
