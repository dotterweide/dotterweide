# Keymap

Currently, the keymap is hardcoded and cannot be configured. This may change in the future.
For now, we try to use the same keyboard mappings as IntelliJ IDEA, with some additional mappings
inspired by SuperCollider. Modifier `menu` corresponds to `ctrl` on Linux (L) and Windows (W), 
and `meta` or command on Mac (M).

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`ESCAPE`           |Clear selection and highlights     |
|`INSERT`           |Toggle insert and overwrite modes  |
|`TAB`              |Increase indentation               |
|`shift TAB`        |Decrease indentation               |
|`menu PLUS`        |Increase font size                 |
|`menu MINUS`       |Decrease font size                 |
|`menu 0`           |Reset font size                    |
|`menu A`           |Select all                         |
|`menu X`           |Cut to clipboard                   |
|`menu C`           |Copy to clipboard                  |
|`menu V`           |Paste from clipboard               |
|`menu Z`           |Undo                               |
|`shift menu Z`     |Redo                               |
|`menu B`           |Go to declaration                  |
|`menu D`           |Duplicate line                     |
|`ctrl Y`           |(L,W) Remove line                  |
|`meta BACK_SPACE`  |(M)   Remove line                  |
|`ctrl UP`          |Scroll up (back)                   |
|`ctrl DOWN`        |Scroll down (forward)              |
|`menu SLASH`       |Toggle line comment                |
|`ctrl SPACE`       |Trigger code completion            |
|`ctrl alt S`       |Toggle vertical split              |
|`shift menu UP`    |Move line up                       |
|`shift menu DOWN`  |Move line down                     |
|`shift menu F7`    |Show usages                        |
|`ctrl LEFT`        |Move to word start                 |
|`ctrl RIGHT`       |Move to word end                   |
|`ctrl BACKSPACE`   |Delete to word start               |
|`ctrl DELETE`      |Delete to word end                 |

## Currently with no Scala support

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`menu alt L`       |Reformat code                      |
|`ctrl alt O`       |Optimize code                      |
|`shift F6`         |Rename refactoring                 |

## Reserved for future functionality

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`F2`               |Go to next error                   |
|`F3`               |(L,W) Find next occurrence         |
|`menu G`           |(M)   Find next occurrence         |
|`F11`              |(L,W) Toggle bookmark              |
|`F3`               |(M)   Toggle bookmark              |
|`shift F2`         |Go to previous error               |
|`shift F3`         |(L,W) Find previous occurrence     |
|`shift menu G`     |(M)   Find previous occurrence     |
|`shift menu SLASH` |Toggle block comment               |
|`menu F`           |Find dialog                        |
|`ctrl F4`          |(L,W) Close editor tab             |
|`ctrl G`           |(L,W) Go to line                   |
|`menu L`           |(M)   Go to line                   |
|`menu R`           |Find-and-replace dialog            |
|`ctrl W`           |(L,W) Extend selection             |
|`shift ctrl W`     |(L,W) Shrink selection             |
|`alt UP`           |(M)   Extend selection             |
|`alt DOWN`         |(M)   Shrink selection             |
|`ctrl alt LEFT`    |Navigate back                      |
|`ctrl alt RIGHT`   |Navigate forward                   |

## Reserved for external applications

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`menu F9`          |Compile or build project           |
|`menu N`           |New file or document               |
|`menu O`           |Open file                          |
|`ctrl alt D`       |Documentation for cursor           |
|`menu alt O`       |Open most recent file              |
|`menu P`           |Show post or log window            |
|`shift menu P`     |Clear post or log window           |
|`menu Q`           |Quit application                   |
|`menu S`           |Save file                          |
|`menu W`           |Close window or file               |
|`shift RETURN`     |Execute selection in interpreter   |
|`shift menu S`     |Save file as                       |
