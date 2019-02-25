/*
 *  LauncherImpl.scala
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

package dotterweide.ide

import javax.swing.SwingUtilities

private class LauncherImpl extends Launcher {
  private[this] var thread: Option[Thread] = None

  def launch(action: => Unit): Unit = {
    thread = Some(new Thread(new MyRunnable(() => action)))
    thread.foreach(_.start())
    notifyObservers()
  }

  def stop(): Unit = {
    thread.foreach(_.stop())
    thread = None
    notifyObservers()
  }

  def active: Boolean = thread.isDefined

  private class MyRunnable(action: () => Unit) extends Runnable {
    def run(): Unit = {
      action()

      SwingUtilities.invokeLater(new Runnable {
        def run(): Unit = stop()
      })
    }
  }
}