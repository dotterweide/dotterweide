//package dotterweide.editor
//
//object Color {
//  val red  : Color = new Color(0xFF, 0x00, 0x00)
//  val green: Color = new Color(0x00, 0xFF, 0x00)
//  val blue : Color = new Color(0x00, 0x00, 0xFF)
//  val white: Color = new Color(0xFF, 0xFF, 0xFF)
//  val gray : Color = new Color(0x80, 0x80, 0x80)
//  val black: Color = new Color(0x00, 0x00, 0x00)
//}
//final case class Color private (value: Int) {
//  def this(red: Int, green: Int, blue: Int, alpha: Int) =
//    this(((alpha & 0xFF) << 24) | ((red & 0xFF) << 16) | ((green & 0xFF) << 8) | ((blue & 0xFF) << 0))
//
//  def this(red: Int, green: Int, blue: Int) =
//    this(red, green, blue, 0xFF)
//
//  def alpha  : Int = (value >> 24) & 0xFF
//  def red    : Int = (value >> 16) & 0xFF
//  def green  : Int = (value >>  8) & 0xFF
//  def blue   : Int =  value        & 0xFF
//
//  def rgb: Int = value
//}
