So in the original code, sizes were hardcoded, assuming that for monospaced 14,
the font advance or `cellWidth` is 8, and the `cellHeight` of 20 is greater than
or equal to the font's height.

The heuristics to obtain the correct data are as follows:

```scala
val frc = new java.awt.font.FontRenderContext(null, true, false)
// val ge  = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment
// ge.getAvailableFontFamilyNames.foreach(println)
// val f   = new java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 14)
val f   = new java.awt.Font("Fira Code Light", java.awt.Font.PLAIN, 14)
f.getStringBounds("X", frc) // w = 8 "advance", h = 16.8, y = -13.1 "-advance"
```

If the font is truly mono-spaced, than the string bounds reported should be
identical for any ascii character.

It seems that the `anti-alias` flag for `FontRenderContext` is not relevant,
but `fractional-metrics` _does_ make a difference. For a programming editor,
we will almost certainly expect not to want fractional metrics, but rather
some form of "hinting". For non-fractional metrics, logical advance will be
integer (no matter how we scale the font height, even floating point heights).
