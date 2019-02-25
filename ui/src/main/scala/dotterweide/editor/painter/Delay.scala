/*
 *  Delay.scala
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

package dotterweide.editor.painter

import dotterweide.ObservableEvents
import dotterweide.editor.{ActionFinished, ActionProcessor, ActionStarted}

/** A collector of `delegate` events, buffering them during the `ActionStarted`/`ActionFinished` window
  * of a `processor`. It is used by [[MatchPainter]].
  */
private class Delay[A](delegate: ObservableEvents[A], processor: ActionProcessor) extends ObservableEvents[A] {
  private[this] var delay = false
  
  private[this] var events = List.empty[A]

  processor.onChange {
    case ActionStarted(_) =>
      delay = true
    case ActionFinished =>
      events.reverseIterator.foreach(notifyObservers)
      events = Nil
      
      delay = false
  }
  
  delegate.onChange { event =>
    if (delay) {
      events ::= event
    } else {
      notifyObservers(event)
    }
  }
}
