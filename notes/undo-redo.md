# Undo-Redo Management

The original `History` implementation is lacking and even wrong.
It's implemented by tracking during `capture` events from both
`Document` and `Terminal`, creating an undoable edit if there is at
least one `DocumentEvent`. For example, if you use the delete key
to delete characters without the terminal moving, then position the
cursor at other location, and then performing undo, the cursor position
is not reset. Furthermore, edits are not merged, as IntelliJ is doing.
In IntelliJ, when you type multiple successive characters, they are
merged into one edit; the same happens with multiple newlines. It
"buffers" the terminal in the sense that when you press undo and there
are "dirty" terminal edits since the last actually undoable (document)
edit, then the terminal is reset first, moving the cursor back to the
last location before the document edit. Only there is no dirty terminal
state, the document edits are undone.

Let's recall the events; terminal state:

 - offset
 - selection
 - hover
 - highlights

Document state:

- insertion, removal, replacement

Terminal state is "transparent" (does not mark the document as dirty),
document state is opaque. Transparent state is attached to opaque state
when forming undoable edits.

We need to rethink the `capture` method. It's kind of the `atomic` of an STM.
We need to understand that when _splitting_ the editor, we gain a second terminal,
so we should not assume a 1:1 coupling between document and terminal.

The original implementation is also wrong here, moving both cursors when undo is performed.
For example, place the cursor of the first view at the end, begin using `delete` key to
remove text in the second view; the models will have corrupt state.

The split view poses interesting problems. Basically we need to treat additional cursors as anchors,
so they are moved automatically if another view inserts or deletes characters before them.
Nevertheless, we have only _one_ undo manager per document.

A look at IntelliJ: 

 - highlights are shared between split views; if focus moves from one view to the other, the
   cursor position is basically switched, causing recalculation of highlights (which makes
   sense as we can only see one cursor at a time)
 - on the other hand, selections are independent
 
---------------------

Considerations:

 - even though they all emit terminal events, there is a categorical difference between
   `offset` and `selection` on one hand, and `highlights` (and `hover`?) on the other. Probably
   in undo/redo highlights should just be cleared, whereas offset and selection need to be tracked.
   
 - observe in IntelliJ: type 'abc', move cursor right, type 'def', move cursor right. The undo sequence now is:
   move cursor left, delete 'def', move cursor left, delete 'abc'. The redo sequence then is the original sequence
   except the final cursor-right.
   
The approach could be similar to swing's concept of `UndoableEdit` with the merger in
`addEdit`/`replaceEdit` and `isSignificant` for transparent/opaque edits. Cursor movement would thus
be not significant. We should strive for immutable edits, so it could be

    def mergeForward(now: Edit): Option[Edit]
    def mergeBackward(before: Edit): Option[Edit]
    
We could gather all offset/selection changes in a combined edit, that is insignificant. When we undo a
non-significant edit, it is simply removed from the queue.
