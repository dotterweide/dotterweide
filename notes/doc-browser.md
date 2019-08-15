# First iteration

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

For IntelliJ: https://github.com/JetBrains/intellij-scala/blob/84f3fcfb74f75b293342f8c55896b0d2e4f549d0/scala/scala-impl/src/org/jetbrains/plugins/scala/editor/documentationProvider/ScalaDocumentationProvider.scala

# Second iteration

We should get rid both of scaladoc and of javafx. scaladoc replacement:

- could be semantic.db eventually (currently doesn't seem to capture comments): https://scalameta.org/docs/semanticdb/specification.html
- could be tasty.doc eventually https://github.com/dotty-staging/dotty/tree/tasty4scalac/tasty4scalac
- current best option (?): extradoc; we did a fork: https://github.com/Sciss/extradoc

this produces JSON for now, but we could probably mangle that into BSON or some other binary format. in the -multi option produces
one JSON per compile unit / object/class/trait.

then we need a way to easily generate a huge source list; probably similar to sbt-unidoc: https://github.com/sbt/sbt-unidoc

the invocation is

        val options = sOpts ++ Opts.doc.externalAPI(xapis)
        val runDoc = Doc.scaladoc(label, s.cacheStoreFactory sub "scala", cs.scalac match {
          case ac: AnalyzingCompiler => ac.onArgs(exported(s, "scaladoc"))
        }, Nil)
        runDoc(srcs, data(cp).toList, out, options, maxErrors, s.log)

with

        type Gen = (Seq[File], Seq[File]      , File, Seq[String], Int      , ManagedLogger)
        runDoc     (srcs     , data(cp).toList, out , options    , maxErrors, s.log)

unidoc plugin basically uses a scope-filter with all projects, then selects all sources in all projects.


