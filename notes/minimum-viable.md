# Minimum Viable Product

In order to get DW into early production, we could aim for a feature parity with
[ScalaInterpreterPane](https://git.iem.at/sciss/ScalaInterpreterPane). That is, any crucial feature
of the latter should be available in the former.

The following editor features should be implemented (note: checkmark means that item has been implemented):

 - [X] FIX: toggle-line-comment broken when several lines are selected
 - [X] the history (undo manager) should be observable, so we can properly track dirty state
 - [X] shift and mouse click should make or extend the selection
 - [X] Configurable wrapping code ("prelude", "postlude")
 - [X] insert/overwrite modes
 - [ ] FIX: skip over closing parentheses; issue #4
 
The following actions and IDE features should be implemented:

 - [X] Code completion
 
The following API features should be implemented:

 - [X] Configuration: colour scheme
 - [X] Configuration: key-map registration (done through actions)
 - [X] Configuration: initial text
 - [X] Editor: current location
 - [X] Editor: selection
 - [X] Editor: flashing
 - [X] Proper shutdown (e.g. stopping Akka actor)

This would work for Mellite. For ScalaCollider-Swing, additionally the following items:

 - [X] customizable font size
 - [ ] possible to disable diagnostic as we run through REPL mode
 - [X] API for symbol-at-cursor, to trigger documentation look-up
 - [ ] performance needs to be acceptable on Raspberry Pi

-----

The following items are "nice to have" but lower priority:

 - [ ] FIX: un-indent does not work when applying to single indented line
 - [ ] Quick Find (ctrl-F)
 - [ ] Editor: find-and-replace dialog
 - [ ] Navigation: jump to matching bracket
 - [ ] Incremental selection of enclosing context (ctrl-w in IntelliJ)
 - [ ] Navigation: jump to previous / next method (alt up/down in IntelliJ)
 - [ ] Block comments (ctrl-shift slash in IntelliJ); if selection start and stop are at beginning of line,
       it inserts a new line before and after block, otherwise it inserts the delimiters directly
 - [ ] Navigation history (ctrl-alt left/right in IntelliJ)
 - [ ] Enter in overwrite mode goes to beginning of new line instead of inserting new line (IntelliJ)
 - [X] FIX: tool tip, error hovers in submin-dark look bad (drop-shadow white text on white)
 