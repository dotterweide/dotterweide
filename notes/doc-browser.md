Using JavaFX WebView is bad for multiple reasons:

- JavaFX as dependency is much more heavy-weight than Swing, especially there is a chaos
  in the transition from JDK 8 to JDK 11, and there is no way to get the JavaFX 
  dependency in a clean cross-platform way, plus OpenJFX 8 is broken in several Linux
  distributions, including Raspbian
- ScalaDoc HTML is opaque, we can't really do anything with that except throw it at
  a fully fledged browser with CSS and JS support
  
A better approach would be something we build ourselves, like IntelliJ's
"Quick Documentation."
  
Here are some pointers from _Metals_ author Ólafur Páll Geirsson:

> have you tried using
> https://github.com/scala/scala/blob/2.13.x/src/scaladoc/scala/tools/nsc/doc/base/CommentFactoryBase.scala ?
> it parses raw docstrings into `Comment`, which you can render into anything you like

And

> we convert docstrings into `SymbolDocumentation`
> https://github.com/scalameta/metals/blob/master/mtags-interfaces/src/main/java/scala/meta/pc/SymbolDocumentation.java
> and have an api to lookup `SymbolDocumentation` given a SemanticDB symbol (which is essentially a string)

