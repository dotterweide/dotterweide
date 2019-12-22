# Keymap

Currently, the keymap is hardcoded and cannot be configured. This may change in the future.
For now, we try to use the same keyboard mappings as IntelliJ IDEA, with some additional mappings
inspired by SuperCollider. Modifier `menu` corresponds to `ctrl` on Linux and Windows, and `meta`
(command) on Mac.

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`ESCAPE`           |Clear selection and highlights     |
|`INSERT`           |Toggle insert and overwrite modes  |
|`TAB`              |Increase indentation               |
|`ctrl 0`           |Reset font size                    |
|`menu A`           |Select all                         |
|`ctrl B`           |Go to declaration                  |
|`menu C`           |Copy to clipboard                  |
|`ctrl D`           |Duplicate line                     |
|`ctrl DOWN`        |Scroll down (forward)              |
|`ctrl MINUS`       |Decrease font size                 |
|`ctrl PLUS`        |Increase font size                 |
|`ctrl SLASH`       |Toggle line comment                |
|`ctrl SPACE`       |Trigger code completion            |
|`ctrl UP`          |Scroll up (back)                   |
|`menu V`           |Paste from clipboard               |
|`menu X`           |Cut to clipboard                   |
|`ctrl Y`           |Remove line                        |
|`menu Z`           |Undo                               |
|`ctrl alt S`       |Toggle vertical split              |
|`shift TAB`        |Decrease indentation               |
|`shift ctrl DOWN`  |Move line down                     |
|`shift ctrl F7`    |Show usages                        |
|`shift ctrl UP`    |Move line up                       |
|`shift menu Z`     |Redo                               |

## Currently with no Scala support

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`ctrl alt L`       |Reformat code                      |
|`ctrl alt O`       |Optimize code                      |
|`shift F6`         |Rename refactoring                 |

## Reserved for future functionality

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`F2`               |Go to next error                   |
|`F3`               |Find next occurrence               |
|`F11`              |Toggle bookmark                    |
|`shift F2`         |Go to previous error               |
|`shift F3`         |Find previous occurrence           |
|`shift ctrl SLASH` |Toggle block comment               |
|`ctrl F`           |Find dialog                        |
|`ctrl F4`          |Close editor tab                   |
|`ctrl G`           |Go to line                         |
|`ctrl R`           |Find-and-replace dialog            |
|?(`ctrl W`)        |Extend selection                   |
|`ctrl alt LEFT`    |Navigate back                      |
|`ctrl alt RIGHT`   |Navigate forward                   |

## Reserved for external applications

|**Key**            |**Action**                         |
|:------------------|:----------------------------------|
|`ctrl F9`          |Compile or build project           |
|`ctrl N`           |New file or document               |
|`ctrl O`           |Open file                          |
|`ctrl alt D`       |Documentation for cursor           |
|`ctrl alt O`       |Open most recent file              |
|`ctrl P`           |Show post or log window            |
|`ctrl Q`           |Quit application                   |
|`ctrl S`           |Save file                          |
|`ctrl W`           |Close window or file               |
|`shift RETURN`     |Execute selection in interpreter   |
|`shift ctrl P`     |Clear post or log window           |
|`shift ctrl S`     |Save file as                       |
