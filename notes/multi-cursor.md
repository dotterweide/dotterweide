# Behaviour as observed in IntelliJ

- there is the 'oldest' and 'newest' cursor; for example, newest cursor determines screen rolling;
  say there are three cursors vertically aligned, if the newest is the top one, and we move upwards,
  scrolling begins as the top one exits the screens, and also if we move downwards, scrolling begins
  when the top cursor exits (thus the other two cursors have already left the screen); "current line"
  highlight also corresponds to the newest cursor.
  
- creation via mouse: alt pressed and drag mouse ("rectangle selection"); cursors are only created
  where they align with the rectangle, if there is a selection, but they are created in every line
  if there is no selection
  
- also you can add cursors with shift-alt click; see
  [this document](https://www.jetbrains.com/help/rider/Multicursor.html)
  
- alternatively, we enter "column selection mode" (alt-shift-insert)

- pressing 'escape' removes all cursors but _the oldest_.

- pasting from clipboard what had been copied/cut in column selection mode / multi cursor mode,
  the result is always inserted as a vertically aligned block, even if the cursor is not at the left
  margin
  
- column selection mode and multi cursor are _two different things_; we can have multiple cursors
  even when column selection is off; they behave differently, for example in column selection, vertically
  aligned cursors stay that way under movement, where when column selection is off, vertically aligned
  cursors may become unaligned.
  
- I suggest to implement column selection mode _later_, as it implies more changes, for example cursors
  can appear at positions greater than the length of a line, so we would need to switch to a position model
  of (line, offset-in-line) instead of absolute offset.

- Or, we change first the position model to (line, offset-in-line).

- cursors 'merge'; for example if one cursor is clipped to start and end of document, and another cursor
  moves "onto the same position"

- there seems no good way to create multiple cursor via keyboard only (not using alt + mouse-drag)?
  common keyboard shortcuts are taken (e.g. ctrl-shift-up/down in Atom Linux or alt-up/down in Atom Mac);
  ctrl-alt up/down ([Sublime](http://www.sublimetext.com/docs/2/multiple_selection_with_the_keyboard.html))
  seems to be available.
  or better, imitate IntelliJ: press Ctrl twice (don't release the second time), then press up or down while
  Ctrl is still held.
  