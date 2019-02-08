/*
 *  UndoableEdit.scala
 *  (Dotterweide)
 *
 *  Copyright (c) 2019 the Dotterweide authors. All rights reserved.
 *
 *  This software is published under the GNU Lesser General Public License v2.1+
 *
 *  For further information, please contact Hanns Holger Rutz at
 *  contact@sciss.de
 */

package dotterweide.editor

/** An edit that can be undone, and then again redone.
  * All sub-classes should be immutable.
  */
trait UndoableEdit {
  def undo(): Unit
  def redo(): Unit

//  def name: String

  /** Tries to merge this edit with a successive edit.
    *
    * @return   `Some` new edit containing both this and the successive edit, if possible,
    *           `None` if merging is not possible
    */
  def tryMerge(succ: UndoableEdit): Option[UndoableEdit]

  /** Whether this edit is destructive to the document model or not. */
  def significant: Boolean
}
