# To-do

## Architecture

 - the `Data` model must be changed to operate asynchronously,
   allowing time to pass for the parser, for example.

## Editor

### basic

 - toggle-line-comment broken when several lines are selected
 - un-indent does not work when applying to single indented line
 - undo manager should coalesce edits (e.g. typing an entire word)
 - find and fine/replace missing
 - optional line-gutter
 - insert/overwrite mode switch
 - go to line/column dialog
 - shift + click, make selection
 
### advanced

 - block selection / multi-cursor
 - vertical indent guides
 - code folding
 - structure breadcrumbs
 - comment with block comment

## Scala

 - we need the node structure to be able to perform most actions,
   such as double-click selection of code (fall back to tokens if
   no structure found)
 
