# Minimum Viable Product

In order to get DW into early production, we could aim for a feature parity with
[ScalaInterpreterPane](https://git.iem.at/sciss/ScalaInterpreterPane). That is, any crucial feature
of the latter should be available in the former.

The following editor features should be implemented (note: checkmark means that item has been implemented):

 - [X] FIX: toggle-line-comment broken when several lines are selected
 - [ ] FIX: un-indent does not work when applying to single indented line
 - [ ] the history (undo manager) should be observable, so we can properly track dirty state
 - [ ] Quick Find (ctrl-F)
 - [X] shift and mouse click should make or extend the selection
 - [ ] Configurable wrapping code ("preamble", "terminator")
 
The following actions and IDE features should be implemented:

 - [ ] Code completion
 
The following API features should be implemented:

 - [ ] Configuration object, e.g. colour scheme, key-map, key-processor, initial text (`CodePane.Config`)
 - [ ] Editor: current location, selection; custom highlight such as flashing (`CodePane`)
 - [ ] Proper shutdown (e.g. stopping Akka actor)

This would work for Mellite. For ScalaCollider-Swing, additionally the following items:

 - [ ] customizable font size
 - [ ] possible to disable diagnostic as we run through REPL mode
 - [ ] API for symbol-at-cursor, to trigger documentation look-up
 - [ ] performance needs to be acceptable on Raspberry Pi

-----

The following features are "nice to have" but lower priority:

 - [ ] Editor: find-and-replace dialog
 - [ ] Navigation: jump to matching bracket
 - [ ] Incremental selection of enclosing context (ctrl-w in IntelliJ)
 