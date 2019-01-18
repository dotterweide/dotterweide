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

package com.pavelfatin.toyide.editor.painter

import com.pavelfatin.toyide.ObservableEvents
import com.pavelfatin.toyide.editor.{ActionFinished, ActionProcessor, ActionStarted}

private class Delay[A](delegate: ObservableEvents[A], processor: ActionProcessor) extends ObservableEvents[A] {
  private var delay = false
  
  private var events = List.empty[A]

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
