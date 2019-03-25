lazy val baseName   = "Dotterweide"
lazy val baseNameL  = baseName.toLowerCase

lazy val projectVersion = "0.1.2-SNAPSHOT"
lazy val mimaVersion    = "0.1.0" // used for migration-manager

lazy val commonSettings = Seq(
  version                   := projectVersion,
  organization              := "de.sciss",  // for now, so we can publish artifacts
  homepage                  := Some(url(s"https://github.com/dotterweide/dotterweide")),
  licenses                  := Seq(lgpl2),
  scalaVersion              := "2.12.8",
  // dispatch/reboot is currently not available for 2.13.0-M5
  crossScalaVersions        := Seq(/* "2.13.0-M5", */ "2.12.8", "2.11.12"),
  scalacOptions            ++= Seq("-deprecation", "-unchecked", "-feature", "-encoding", "utf8", "-Xlint", "-Xsource:2.13"),
  fork in Test              := false,
  fork in (Compile, run)    := true,
  parallelExecution in Test := false
)

lazy val lgpl2  = "LGPL v2.1+"  -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")
lazy val gpl3   = "GPL v3+"     -> url("http://www.gnu.org/licenses/gpl-3.0.txt")

lazy val deps = new {
  val main = new {
    val akka            = "2.5.19"  // "2.5.21" has broken printDebugDump
    val dispatch        = "1.0.0"
    val scalariform     = "0.2.7"
    val scalaSwing      = "2.1.0"
  }
  val demo = new {
    val scopt           = "3.7.1"
    val submin          = "0.2.4"
  }
  val test = new {
    val junit           = "4.12"
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
      "org.scalariform"   %% "scalariform"    % deps.main.scalariform
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

lazy val docBrowser = project.withId(s"$baseNameL-doc-browser").in(file("doc-browser"))
  .dependsOn(core)
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(testSettings)
  .settings(
    name        := s"$baseName-Doc-Browser",
    description := s"$baseName - documentation browser",
    libraryDependencies ++= Seq(
      "org.dispatchhttp" %% "dispatch-core" % deps.main.dispatch // downloading of http resources
    ),
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
      "com.github.scopt"  %% "scopt"  % deps.demo.scopt,
      "de.sciss"          %  "submin" % deps.demo.submin
    )
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
