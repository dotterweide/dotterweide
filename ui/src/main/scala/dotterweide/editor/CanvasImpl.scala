/*
 *  CanvasImpl.scala
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

package dotterweide.editor

import java.awt.event.{AdjustmentEvent, AdjustmentListener, ComponentAdapter, ComponentEvent, FocusEvent, FocusListener, HierarchyEvent, HierarchyListener}
import java.awt.{Dimension, Rectangle}

import javax.swing.{JComponent, JScrollPane}

private class CanvasImpl(component: JComponent, scrollPane: JScrollPane) extends Canvas {
  private var _caretVisible = false

  component.addHierarchyListener(new HierarchyListener {
    override def hierarchyChanged(e: HierarchyEvent): Unit =
      if ((e.getChangeFlags & HierarchyEvent.SHOWING_CHANGED) > 0) {
        notifyObservers(VisibilityChanged(component.isShowing))
      }
  })

  private val scrollListener = new AdjustmentListener {
    def adjustmentValueChanged(e: AdjustmentEvent): Unit =
      notifyObservers(VisibleRectangleChanged(component.getVisibleRect))
  }

  scrollPane.getVerticalScrollBar   .addAdjustmentListener(scrollListener)
  scrollPane.getHorizontalScrollBar .addAdjustmentListener(scrollListener)

  component.addComponentListener(new ComponentAdapter {
    override def componentResized(e: ComponentEvent): Unit =
      notifyObservers(VisibleRectangleChanged(component.getVisibleRect))
  })

  component.addFocusListener(new FocusListener {
    def focusGained (e: FocusEvent): Unit = notifyObservers(FocusChanged(true ))
    def focusLost   (e: FocusEvent): Unit = notifyObservers(FocusChanged(false))
  })

  def size: Dimension = component.getSize

  def visible: Boolean = component.isShowing

  def visibleRectangle: Rectangle = component.getVisibleRect

  def hasFocus: Boolean = component.hasFocus

  def caretVisible: Boolean = _caretVisible

  def caretVisible_=(b: Boolean): Unit =
    if (_caretVisible != b) {
      _caretVisible = b
      notifyObservers(CaretVisibilityChanged(b))
    }
}
