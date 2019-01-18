package dotterweide

import java.awt.{Color => AWTColor}

package object editor {
  // TODO --- we should get rid of AWT dependencies in core module
  object Color {
    val red  : Color = AWTColor.red
    val green: Color = AWTColor.green
    val blue : Color = AWTColor.blue
    val white: Color = AWTColor.white
    val gray : Color = AWTColor.gray
    val black: Color = AWTColor.black
  }
  type Color = AWTColor
}
