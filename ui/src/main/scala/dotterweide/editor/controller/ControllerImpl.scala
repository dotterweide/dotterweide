/*
 *  ControllerImpl.scala
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

package dotterweide.editor.controller

import java.awt.AWTKeyStroke
import java.awt.event.{KeyEvent, MouseEvent}

import dotterweide.Interval
import dotterweide.document.Document
import dotterweide.editor.ControllerOps._
import dotterweide.editor.{Action, ActionFinished, ActionStarted, Adviser, Async, Data, EditorActions, FontSettings, Grid, History, NamedEdit, Terminal}
import dotterweide.formatter.Formatter
import dotterweide.node.{IdentifiedNode, Node}

import scala.collection.immutable.{Seq => ISeq}

/** Implements `Controller`
  *
  * - control and mouse hover set terminal's `hover`
  */
class ControllerImpl(document: Document, data: Data, terminal: Terminal, grid: Grid, adviser: Adviser,
                     formatter: Formatter, tabSize: Int, lineCommentPrefix: String, font: FontSettings,
                     history: History)
                    (implicit async: Async)
  extends Controller {

  // XXX TODO extract to some extension (maybe using brace matcher)
  private[this] val pairs = List(
    ('(', ')'),
    ('[', ']'),
    ('{', '}'),
    ('\"', '\"')
  )

  private[this] val BlockOpening = '{'
  private[this] val BlockClosing = '}'

  private[this] var origin = 0

  val actions: EditorActions =
    new Actions(document, terminal, data, adviser, formatter, tabSize = tabSize,
      lineCommentPrefix = lineCommentPrefix, font = font, history = history)

  private[this] var actionSeq: ISeq[Action] = actions.all

  def addAction(a: Action): Unit =
    actionSeq = a +: actionSeq

  def removeAction(a: Action): Unit =
    actionSeq = actionSeq.filterNot(_ == a)

  def processKeyPressed(e: KeyEvent): Unit = {
    if (isModifierKey(e.getKeyCode)) return

    notifyObservers(ActionStarted(isImmediate(e)))

    doProcessKeyPressed(e)

    processActions(e)

    notifyObservers(ActionFinished)
  }

  def processKeyTyped(e: KeyEvent): Unit = {
    notifyObservers(ActionStarted(immediate = true))

    doProcessKeyTyped(e)

    notifyObservers(ActionFinished)
  }

  def processActions(e: KeyEvent): Unit = {
    val keyStroke = AWTKeyStroke.getAWTKeyStroke(e.getKeyCode, e.getModifiers).toString

    // XXX TODO --- this is very inefficient
    for {
      action <- actionSeq
      if action.enabled && action.keys.contains(keyStroke)
    } {
      action()
      e.consume()
    }
  }

  /** `true` if pressing back-space or delete */
  private def isImmediate(e: KeyEvent): Boolean = e.getKeyCode match {
    case KeyEvent.VK_BACK_SPACE | KeyEvent.VK_DELETE => true
    case _ => false
  }

  private def capture[A](name: String)(body: => A): A =
    history.capture(name, document, terminal)(body)

  /* Handles cursor movement and back-space/delete */
  private def doProcessKeyPressed(e: KeyEvent): Unit = {
    if (e.isShiftDown && terminal.selection.isEmpty) origin = terminal.offset

    def move(body: => Unit): Unit = capture("Move")(body)

    e.getKeyCode match {
      case KeyEvent.VK_LEFT =>
        if (terminal.offset > 0) move {
          if (e.isControlDown) {
            terminal.offset = seek(-1)
          } else {
            terminal.offset = terminal.selection.filter(_ => !e.isShiftDown).fold(terminal.offset - 1)(_.start)
          }
          terminal.selection = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_RIGHT =>
        if (terminal.offset < document.length) move {
          if (e.isControlDown) {
            terminal.offset = seek(1)
          } else {
            terminal.offset = terminal.selection.filter(_ => !e.isShiftDown).fold(terminal.offset + 1)(_.stop)
          }
          terminal.selection = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_UP if !e.isControlDown =>
        if (document.lineNumberOf(terminal.offset) > 0) move {
          jumpTo(document.lineNumberOf(terminal.offset) - 1, e.isShiftDown)
        }

      case KeyEvent.VK_DOWN if !e.isControlDown =>
        if (document.lineNumberOf(terminal.offset) < document.linesCount - 1) move {
          jumpTo(document.lineNumberOf(terminal.offset) + 1, e.isShiftDown)
        }

      case KeyEvent.VK_PAGE_UP if !e.isControlDown =>
        if (document.lineNumberOf(terminal.offset) > 0) move {
          jumpTo(0.max(document.lineNumberOf(terminal.offset) - 10), e.isShiftDown)
        }

      case KeyEvent.VK_PAGE_DOWN if !e.isControlDown =>
        if (document.lineNumberOf(terminal.offset) < document.linesCount - 1) move {
          jumpTo((document.linesCount - 1).min(document.lineNumberOf(terminal.offset) + 10), e.isShiftDown)
        }

      case KeyEvent.VK_HOME if e.isControlDown =>
        move {
          terminal.offset = 0
          terminal.selection = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_END if e.isControlDown =>
        move {
          terminal.offset = document.length
          terminal.selection = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_HOME =>
        move {
          val origin = terminal.offset
          val edge = document.startOffsetOf(document.lineNumberOf(terminal.offset))
          val next = seek(c => c.isWhitespace, edge, 1)
            .filter(document.toLocation(_).line == document.toLocation(edge).line)
            .getOrElse(edge)
          terminal.offset     = if (next == origin) edge else next
          terminal.selection  = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_END =>
        move {
          terminal.offset     = document.endOffsetOf(document.lineNumberOf(terminal.offset))
          terminal.selection  = if (e.isShiftDown) fromOriginTo(terminal.offset) else None
        }

      case KeyEvent.VK_BACK_SPACE =>
        def backspace(interval: Interval): Unit = {
          val edit = Backspace(document, terminal, interval)
          history.add(edit)
        }

        terminal.selection match {
          case Some(sel) => backspace(sel)

          case None if terminal.offset > 0 =>
            val off             = terminal.offset
            val length          = if (e.isControlDown) off - seek(-1) else 1
            val remStart        = off - length
            val leftChar        = document.charAt(off - 1)
            val rightChar       = if (document.length > off) document.charAt(off) else '?'
            val complement      = pairs.contains((leftChar, rightChar))
            val remStop         = if (complement) off + 1 else off
            backspace(Interval(remStart, remStop))

          case _ =>
        }

      case KeyEvent.VK_DELETE if !e.isShiftDown =>
        def delete(interval: Interval): Unit = {
          val edit = Delete(document, terminal, interval)
          history.add(edit)
        }

        terminal.selection match {
          case Some(sel) => delete(sel)

          case None if terminal.offset < document.length =>
            val off       = terminal.offset
            val length    = if (e.isControlDown) seek(1) - off else 1
            val remStart  = off
            val remStop   = off + length
            delete(Interval(remStart, remStop))

          case _ =>
        }

      case KeyEvent.VK_INSERT =>
        terminal.overwriteMode = !terminal.overwriteMode

      case _ =>
    }
  }

  private def jumpTo(targetLine: Int, shiftPressed: Boolean): Unit = {
    val line            = document.lineNumberOf(terminal.offset)
    val indent          = terminal.offset - document.startOffsetOf(line)
    val target          = document.startOffsetOf(targetLine) + indent
    terminal.offset     = target.min(document.endOffsetOf(targetLine))
    terminal.selection  = if (shiftPressed) fromOriginTo(terminal.offset) else None
  }

  private def doProcessKeyTyped(e: KeyEvent): Unit = {
    def insert(body: => Unit): Unit = capture("Typing")(body)

    e.getKeyChar match {
      case c if c == KeyEvent.VK_ENTER && !e.isAltDown && !e.isShiftDown =>
        if (terminal.selection.isDefined) insert {
          processCharInsertion(c)
        } else {
          val edit = mkLineNew(hold = e.isControlDown)
          history.add(edit)
        }

      case c if c == KeyEvent.VK_TAB && !e.isControlDown && !e.isShiftDown && terminal.selection.isEmpty =>
        val chars = " " * tabSize
        val edit = typing(chars, advance = chars.length)
        history.add(edit)

      case c if !c.isControl && !e.isControlDown && !e.isAltDown =>
        if (isClosingPair(c)) {
          insert { terminal.offset += 1 }
        } else {
          val chars = mkInsertionChars(c)

          @inline
          def perform() = typing(chars, overwrite = terminal.overwriteMode)

          if (isClosingBlock(c)) insert {
            processCloseBlock(c)
            perform()
          } else {
            val edit = perform()
            history.add(edit)
          }
        }

      case _ =>
    }
  }

  private def mkLineNew(hold: Boolean): NewLine = {
    val oldOff  = terminal.offset
    val n       = document.lineNumberOf(terminal.offset)
    val prefix  = document.text(document.startOffsetOf(n), terminal.offset)
    val suffix  = document.text(terminal.offset, document.endOffsetOf(n))
    val i1      = indentOf(n)
    val i2      = if (prefix.trim.endsWith(BlockOpening.toString)) tabSize else 0
    val i3      = document.charOptionAt(terminal.offset).filter(_ == BlockClosing).fold(0)(_ => tabSize)
    val shift   = suffix.takeWhile(_.isWhitespace).length
    val indent  = 0.max((if (i2 == 0 && i3 > i1) i1 + i2 - i3 else i1 + i2) - shift)
    var chars   = "\n" + Seq.fill(indent)(' ').mkString
    if (i2 > 0 && i3 > 0) chars += "\n" + Seq.fill(i1 + i2 - i3)(' ').mkString
    val newOff  = if (hold) oldOff else oldOff + indent + 1
    NewLine(document, terminal, chars, offsetAfter = newOff)
  }

  def processEnterPressed(hold: Boolean = false): Unit = {
    mkLineNew(hold = hold)
  }

  // XXX TODO --- would be great if we could also find an early matching opening mark
  private def isClosingPair(c: Char): Boolean = {
    val nextChar = document.charAtOrElse(terminal.offset    , '?')
    val prevChar = document.charAtOrElse(terminal.offset - 1, '?')
    (nextChar == c) && pairs.contains((prevChar, nextChar))
  }

  private def isClosingBlock(c: Char): Boolean =
    c == BlockClosing

  private def processCloseBlock(c: Char): Unit = {
    val indent        = terminal.offset - document.startOffsetOf(document.lineNumberOf(terminal.offset))
    val targetIndent  = 0.max(indentFrom(document.lineNumberOf(terminal.offset)) - tabSize)
    if (indent > targetIndent) {
      val d = indent - targetIndent
      if (document.text(terminal.offset - d, terminal.offset).forall(_.isWhitespace)) {
        terminal.offset -= d
        document.remove(terminal.offset, terminal.offset + d)
      }
    } else if (indent < targetIndent) {
      val d = targetIndent - indent
      document.insert(terminal.offset, Seq.fill(d)(' ').mkString)
      terminal.offset += d
    }
  }

  private def mkInsertionChars(c: Char): String = {
    val nextChar = document.charAtOrElse(terminal.offset, '?')
    if (nextChar.isLetterOrDigit || nextChar == c) c.toString
    else pairs.collectFirst {
      case (`c`, close) => s"$c$close"
    } .getOrElse(c.toString)
  }

  private def typing(chars: String, advance: Int = 1, overwrite: Boolean = false): NamedEdit =
    if (overwrite) {
      Overwrite (document, terminal, chars, advance = advance)
    } else {
      Insert    (document, terminal, chars, advance = advance)
    }

  // XXX TODO --- this public method here is mainly for the tests
  def processCharInsertion(c: Char): Unit = {
    // if we are closing a pair, simply "overwrite" the closing character by moving the cursor to the right
    if (isClosingPair(c)) {
      terminal.offset += 1
    } else {
      val s = mkInsertionChars(c)
      if (isClosingBlock(c)) {
        processCloseBlock(c)
      }
      typing(s)
    }
  }

  def processMousePressed(e: MouseEvent): Unit =
    if (e.isShiftDown) processMouseDragged(e) else processMousePressedNoShift(e)

  private def processMousePressedNoShift(e: MouseEvent): Unit = {
    val navigation      = (e.getButton == MouseEvent.BUTTON1 && e.isControlDown) || e.getButton == MouseEvent.BUTTON2
    val targetOffset    = if (navigation) {
      for {
        i         <- document.toOffset(grid.toLocation(e.getPoint))
        reference <- data.referenceAt(i)
        target    <- reference.target
      } yield offsetOf(target)
    } else {
      None
    }
    val pointOffset     = document.toNearestOffset(grid.toLocation(e.getPoint))
    val leafSpan        = if (e.getButton == MouseEvent.BUTTON1 && e.getClickCount == 2) {
      val leafOpt = data.leafAt(pointOffset)
      leafOpt.map(_.span)
    } else {
      None
    }
    terminal.offset     = targetOffset.orElse(leafSpan.map(_.stop)).getOrElse(pointOffset)
    origin              = leafSpan.fold(terminal.offset)(_.start)
    terminal.selection  = leafSpan.map(_.interval)
  }

  def processMouseDragged(e: MouseEvent): Unit = {
    val pointOffset     = document.toNearestOffset(grid.toLocation(e.getPoint))
    terminal.offset     = pointOffset
    val start           = math.min(origin, pointOffset)
    val stop            = math.max(origin, pointOffset)
    terminal.selection  = Some(Interval(start, stop))
  }

  def processMouseMoved(e: MouseEvent): Unit = {
    val hover = if (e.isControlDown) {
      document.toOffset(grid.toLocation(e.getPoint))
    } else {
      None
    }
    // avoid frequent event notifications
    if (terminal.hover != hover) terminal.hover = hover
  }

  // TODO remove this duplicate
  private def offsetOf(target: Node): Int = {
    target match {
      case IdentifiedNode(id, _) => id.span.start
      case node => node.span.start
    }
  }

  private def fromOriginTo(offset: Int) = {
    val offsets = Seq(origin, offset).sorted
    Some(Interval(offsets(0), offsets(1)))
  }

  private def isModifierKey(c: Int) = c match {
    case KeyEvent.VK_SHIFT | KeyEvent.VK_CONTROL | KeyEvent.VK_ALT | KeyEvent.VK_META => true
    case _ => false
  }

  private def indentOf(line: Int): Int = {
    if (line < 0) 0 else {
      val s = document.text(document.startOffsetOf(line), document.endOffsetOf(line))
      if (s.trim.isEmpty) indentFrom(line - 1) else s.takeWhile(_.isWhitespace).length
    }
  }

  private def indentFrom(line: Int): Int = {
    if (line < 0) 0 else {
      val s = document.text(document.startOffsetOf(line), document.endOffsetOf(line))
      if (s.trim.isEmpty) indentFrom(line - 1) else
        s.takeWhile(_.isWhitespace).length + (if (s.trim.endsWith("{")) tabSize else 0)
    }
  }

  private def seek(increment: Int): Int = {
    val predicates  = List[Char => Boolean](_.isWhitespace, _.isLetter, _.isDigit)
    val other       = (c: Char) => predicates.forall(!_(c))
    val target      = (other :: predicates).reverse.view.flatMap(seek(_, terminal.offset, increment).toSeq)
    target.headOption.getOrElse(terminal.offset + increment)
  }

  private def seek(predicate: Char => Boolean, start: Int, increment: Int): Option[Int] = {
    def charAt(i: Int) = document.charAt(if (increment == -1) i - 1 else i)
    var index = start
    if (predicate(charAt(index))) {
      def target = index + increment
      while (target >= 0 && target <= document.length && predicate(charAt(index))) index += increment
      Some(index)
    } else {
      None
    }
  }
}