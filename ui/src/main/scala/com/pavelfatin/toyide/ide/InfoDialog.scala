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

package com.pavelfatin.toyide.ide

import java.awt.{Desktop, Graphics}
import javax.swing.border.{CompoundBorder, EmptyBorder, EtchedBorder}
import javax.swing.event.{HyperlinkEvent, HyperlinkListener}
import javax.swing.text.html.{HTMLEditorKit, ImageView}
import javax.swing.text.{AbstractDocument, Element, View, ViewFactory}
import javax.swing.{JComponent, KeyStroke, WindowConstants}

import scala.swing.{Action, BorderPanel, Button, Dialog, Dimension, EditorPane, Insets, ScrollPane, Window}

private class InfoDialog(owner: Window, file: String, scrolling: Boolean) extends Dialog(owner) {
  modal = true
  title = "About the program"

  private val pane = new EditorPane() {
    focusable = false
    border = {
      val spacer = new EmptyBorder(5, 10, 10, 10)
      if (scrolling) spacer else new CompoundBorder(new EtchedBottomBorder(), spacer)
    }
    editorKit = new SynchronousHTMLEditorKit()
    peer.addHyperlinkListener(new LinkRedirector())
    peer.setPage(getClass.getResource("/%s".format(file)))
    editable = false
  }

  private val action = new Action("OK") {
    def apply(): Unit = dispose()
  }

  private val button = new Button(action) {
    preferredSize = new Dimension(85, preferredSize.height)
  }

  private val footer = new BorderPanel() {
    border = new EmptyBorder(5, 5, 5, 5)
    add(button, BorderPanel.Position.East)
  }

  contents = new BorderPanel() {
    add(if (scrolling) new ScrollPane(pane) else pane, BorderPanel.Position.Center)
    add(footer, BorderPanel.Position.South)
  }

  defaultButton = button

  peer.getRootPane.registerKeyboardAction(action.peer,
    KeyStroke.getKeyStroke("pressed ESCAPE"), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)

  peer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
}

private class EtchedBottomBorder extends EtchedBorder {
  override def paintBorder(c: java.awt.Component, g: Graphics, x: Int, y: Int, width: Int, height: Int): Unit = {
    g.translate(x, y)
    g.setColor(if (etchType == EtchedBorder.LOWERED) getShadowColor(c) else getHighlightColor(c))
    g.drawLine(0, height - 2, width - 1, height - 2)
    g.setColor(if (etchType == EtchedBorder.LOWERED) getHighlightColor(c) else getShadowColor(c))
    g.drawLine(0, height - 1, width - 1, height - 1)
    g.translate(-x, -y)
  }

  override def getBorderInsets(c: java.awt.Component) = new Insets(0, 0, 2, 0)

  override def getBorderInsets(c: java.awt.Component, insets: java.awt.Insets): Insets = {
    insets.left   = 0
    insets.top    = 0
    insets.right  = 0
    insets.bottom = 2
    insets
  }
}

private class LinkRedirector extends HyperlinkListener {
  def hyperlinkUpdate(event: HyperlinkEvent): Unit =
    event.getEventType match {
      case HyperlinkEvent.EventType.ACTIVATED => Desktop.getDesktop.browse(event.getURL.toURI)
      case _ =>
    }
}

private class SynchronousHTMLEditorKit extends HTMLEditorKit {
  override def createDefaultDocument: AbstractDocument = {
    val doc = super.createDefaultDocument.asInstanceOf[AbstractDocument]
    doc.setAsynchronousLoadPriority(-1)
    doc
  }

  override def getViewFactory = new SynchronousImageViewFactory(super.getViewFactory)
}

private class SynchronousImageViewFactory(impl: ViewFactory) extends ViewFactory {
  def create(elem: Element): View =
    impl.create(elem) match {
      case iv: ImageView =>
        iv.setLoadsSynchronously(true)
        iv
      case it => it
    }
}