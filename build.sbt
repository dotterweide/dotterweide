lazy val baseName   = "Dotterweide"
lazy val baseNameL  = baseName.toLowerCase

lazy val projectVersion = "0.4.0"
lazy val mimaVersion    = "0.4.0" // used for migration-manager

lazy val commonSettings = Seq(
  version                   := projectVersion,
  organization              := "de.sciss",  // for now, so we can publish artifacts
  homepage                  := Some(url(s"https://github.com/dotterweide/dotterweide")),
  licenses                  := Seq(lgpl2),
  scalaVersion              := "2.13.3",
  crossScalaVersions        := Seq("2.13.3", "2.12.12"),
  scalacOptions            ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  fork in Test              := false,
  fork in (Compile, run)    := true,
  parallelExecution in Test := false,
  unmanagedSourceDirectories in Compile += {
    val sourceDir = (sourceDirectory in Compile).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  scalacOptions in (Compile, compile) ++= (if (scala.util.Properties.isJavaAtLeast("9")) Seq("-release", "8") else Nil), // JDK >8 breaks API; skip scala-doc
)

lazy val lgpl2  = "LGPL v2.1+"  -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")
lazy val gpl3   = "GPL v3+"     -> url("http://www.gnu.org/licenses/gpl-3.0.txt")

// lazy val dispatchOrg = "org.dispatchhttp"
lazy val dispatchOrg = "de.sciss"

// lazy val scalariformOrg = "org.scalariform"
lazy val scalariformOrg = "de.sciss"

lazy val deps = new {
  val main = new {
    val akka            = "2.6.10"
    // val dispatch        = "1.0.1"
    val dispatch        = "0.1.1"
    val scalariform     = "0.2.8"
    val scalaSwing      = "3.0.0"
  }
  val demo = new {
    val scallop         = "3.5.1"
    val submin          = "0.3.4"
  }
  val test = new {
    val junit           = "4.13.1"
    val junitInterface  = "0.11"
  }
}

lazy val testSettings = Seq(
  libraryDependencies ++= Seq(
    "junit"        % "junit"           % deps.test.junit          % Test,
    "com.novocode" % "junit-interface" % deps.test.junitInterface % Test
  )
)

lazy val root = project.withId(baseNameL).in(file("."))
  .aggregate(core, lispLang, toyLang, scalaLang, ui, docBrowser, demo)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    name        := baseName,
    description := s"$baseName - Embeddable mini-IDE"
  )

lazy val core = project.withId(s"$baseNameL-core").in(file("core"))
  .settings(commonSettings)
  .settings(testSettings)
  .settings(publishSettings)
  .settings(
    name        := s"$baseName-Core",
    description := s"$baseName - Core API",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % deps.main.scalaSwing
    ),
    mimaPreviousArtifacts := Set("de.sciss" %% s"$baseNameL-core" % mimaVersion)
  )

lazy val lispLang = project.withId(s"$baseNameL-lisp").in(file("lisp"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Lisp",
    description := s"$baseName - Clojure-like functional language",
    unmanagedResourceDirectories in Compile += baseDirectory.value / "src" / "main" / "lisp"
  )

lazy val toyLang = project.withId(s"$baseNameL-toy").in(file("toy"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Toy",
    description := s"$baseName - C-like imperative language"
  )

lazy val scalaLang = project.withId(s"$baseNameL-scala").in(file("scala"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Scala",
    description := s"$baseName - Scala language",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor"     % deps.main.akka,
      "org.scala-lang"    %  "scala-compiler" % scalaVersion.value,
      scalariformOrg      %% "scalariform"    % deps.main.scalariform
    ),
    mimaPreviousArtifacts := Set("de.sciss" %% s"$baseNameL-scala" % mimaVersion)
  )

lazy val ui = project.withId(s"$baseNameL-ui").in(file("ui"))
  .dependsOn(core % "compile->compile;test->test")
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-UI",
    description := s"$baseName - graphical user interface",
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-swing" % deps.main.scalaSwing
    ),
    mimaPreviousArtifacts := Set("de.sciss" %% s"$baseNameL-ui" % mimaVersion)
  )

// Determine OS version of JavaFX binaries
lazy val jfxClassifier = sys.props("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

// N.B. we use `Provided` here so we can publish
// neutral Maven artifacts. This is all a big shite,
// so we really need to get rid of JavaFX soon.
def jfxDep(name: String): ModuleID =
  ("org.openjfx" % s"javafx-$name" % "11.0.2" % Provided).classifier(jfxClassifier)

lazy val docBrowser = project.withId(s"$baseNameL-doc-browser").in(file("doc-browser"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Doc-Browser",
    description := s"$baseName - documentation browser",
    libraryDependencies ++= Seq(
      dispatchOrg %% "dispatch-core" % deps.main.dispatch // downloading of http resources
    ),
    libraryDependencies ++= Seq("base", "swing", "controls", "graphics", "media", "web").map(jfxDep),
    mimaPreviousArtifacts := Set("de.sciss" %% s"$baseNameL-doc-browser" % mimaVersion)
  )

lazy val demo = project.withId(s"$baseNameL-demo").in(file("demo"))
  .dependsOn(ui, lispLang, toyLang, scalaLang, docBrowser)
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    name        := s"$baseName-Demo",
    description := s"$baseName - demo application",
    licenses    := Seq(gpl3),
    mainClass in Compile := Some("dotterweide.Demo"),
    libraryDependencies ++= Seq(
      "de.sciss"   %  "submin"  % deps.demo.submin,
      "org.rogach" %% "scallop" % deps.demo.scallop
    ),
    libraryDependencies ++= Seq("base", "swing", "controls", "graphics", "media", "web").map(jfxDep),
  )

// ---- publishing ----
lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = baseNameL
<scm>
  <url>git@git.github.com:dotterweide/{n}.git</url>
  <connection>scm:git:git@git.github.com:dotterweide/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
  }
)

lazy val noPublishSettings = Seq(
  skip in publish := true
)
